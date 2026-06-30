# Security Hardening Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add lightweight security hardening for SQL-injection-style inputs, URL tampering, and unsafe GET data-changing actions.

**Architecture:** Keep the current Session-based authentication architecture and add focused validation/error-handling utilities. Controllers validate and sanitize request parameters before calling services; unsafe data-changing operations move from GET links to POST forms.

**Tech Stack:** Spring Boot 3.2.5, Spring MVC, MyBatis, Thymeleaf, Maven, Java 17.

---

## File Structure

### New files

- `src/main/java/com/sportsmeet/common/BusinessException.java`
  - Runtime exception for expected business/security validation failures.
- `src/main/java/com/sportsmeet/common/SecurityValidator.java`
  - Static validation and sanitization helpers for IDs, keywords, enum values, ranks, scores, and form text.
- `src/main/java/com/sportsmeet/common/GlobalExceptionHandler.java`
  - Handles common MVC/API exceptions and avoids Whitelabel pages for expected invalid requests.

### Modified files

- `src/main/java/com/sportsmeet/controller/EventController.java`
  - Sanitize list filters, validate event form fields, convert delete/finish to POST.
- `src/main/java/com/sportsmeet/controller/AthleteController.java`
  - Sanitize list filters, validate athlete form fields, convert delete to POST.
- `src/main/java/com/sportsmeet/controller/RegistrationController.java`
  - Sanitize list filters, validate IDs, convert cancel to POST.
- `src/main/java/com/sportsmeet/controller/ScoreController.java`
  - Validate event IDs and submitted score arrays.
- `src/main/java/com/sportsmeet/controller/ReportController.java`
  - Sanitize keyword and validate event IDs.
- `src/main/java/com/sportsmeet/controller/ApiController.java`
  - Sanitize API request params and return safe JSON errors.
- `src/main/resources/templates/event/list.html`
  - Replace delete/finish links with POST forms.
- `src/main/resources/templates/athlete/list.html`
  - Replace delete link with POST form.
- `src/main/resources/templates/registration/list.html`
  - Replace cancel link with POST form.

---

## Task 1: Add Security Utility Classes

**Files:**
- Create: `src/main/java/com/sportsmeet/common/BusinessException.java`
- Create: `src/main/java/com/sportsmeet/common/SecurityValidator.java`

- [ ] **Step 1: Create BusinessException**

Create `src/main/java/com/sportsmeet/common/BusinessException.java`:

```java
package com.sportsmeet.common;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

- [ ] **Step 2: Create SecurityValidator**

Create `src/main/java/com/sportsmeet/common/SecurityValidator.java`:

```java
package com.sportsmeet.common;

import java.util.List;
import java.util.regex.Pattern;

public final class SecurityValidator {

    private static final Pattern DANGEROUS_PATTERN = Pattern.compile("('|\"|;|--|/\\*|\\*/|<script|</script|javascript:)", Pattern.CASE_INSENSITIVE);
    private static final List<String> CATEGORIES = List.of("男子", "女子", "混合");
    private static final List<String> GENDERS = List.of("男", "女");

    private SecurityValidator() {
    }

    public static Long validId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BusinessException(fieldName + "不合法");
        }
        return id;
    }

    public static String cleanKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String value = keyword.trim();
        if (value.isEmpty()) {
            return null;
        }
        if (value.length() > 30) {
            throw new BusinessException("搜索关键词不能超过30个字符");
        }
        if (DANGEROUS_PATTERN.matcher(value).find()) {
            throw new BusinessException("搜索关键词包含非法字符");
        }
        return value.replace("%", "").replace("_", "");
    }

    public static String validCategory(String category, boolean required) {
        if (category == null || category.trim().isEmpty()) {
            if (required) {
                throw new BusinessException("比赛类别不能为空");
            }
            return null;
        }
        String value = category.trim();
        if (!CATEGORIES.contains(value)) {
            throw new BusinessException("比赛类别不合法");
        }
        return value;
    }

    public static String validGender(String gender) {
        if (gender == null || !GENDERS.contains(gender.trim())) {
            throw new BusinessException("性别不合法");
        }
        return gender.trim();
    }

    public static Integer validStatus(Integer status) {
        if (status == null || status < 0 || status > 2) {
            throw new BusinessException("状态不合法");
        }
        return status;
    }

    public static Integer validTopN(Integer topN) {
        if (topN == null || topN < 1 || topN > 10) {
            throw new BusinessException("取前N名范围必须是1到10");
        }
        return topN;
    }

    public static Integer validRank(Integer rank) {
        if (rank == null || rank < 1 || rank > 10) {
            throw new BusinessException("名次范围必须是1到10");
        }
        return rank;
    }

    public static String validText(String text, String fieldName, int maxLength) {
        if (text == null || text.trim().isEmpty()) {
            throw new BusinessException(fieldName + "不能为空");
        }
        String value = text.trim();
        if (value.length() > maxLength) {
            throw new BusinessException(fieldName + "不能超过" + maxLength + "个字符");
        }
        if (DANGEROUS_PATTERN.matcher(value).find()) {
            throw new BusinessException(fieldName + "包含非法字符");
        }
        return value;
    }

    public static String validOptionalText(String text, String fieldName, int maxLength) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        return validText(text, fieldName, maxLength);
    }
}
```

- [ ] **Step 3: Compile utility classes**

Run:

```powershell
mvn compile -DskipTests
```

Expected: `BUILD SUCCESS`.

---

## Task 2: Add Global Exception Handling

**Files:**
- Create: `src/main/java/com/sportsmeet/common/GlobalExceptionHandler.java`

- [ ] **Step 1: Create GlobalExceptionHandler**

Create `src/main/java/com/sportsmeet/common/GlobalExceptionHandler.java`:

```java
package com.sportsmeet.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:" + fallbackPath(request.getRequestURI());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    public String handleBadRequest(Exception e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "参数格式不正确");
        return "redirect:" + fallbackPath(request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "系统异常，请稍后再试");
        return "redirect:" + fallbackPath(request.getRequestURI());
    }

    private String fallbackPath(String uri) {
        if (uri == null) {
            return "/dashboard";
        }
        if (uri.startsWith("/event")) {
            return "/event/list";
        }
        if (uri.startsWith("/athlete")) {
            return "/athlete/list";
        }
        if (uri.startsWith("/registration")) {
            return "/registration/list";
        }
        if (uri.startsWith("/score")) {
            return "/score/input";
        }
        if (uri.startsWith("/report/event")) {
            return "/report/event";
        }
        if (uri.startsWith("/report/personal")) {
            return "/report/personal";
        }
        if (uri.startsWith("/report")) {
            return "/report/team";
        }
        return "/dashboard";
    }
}
```

- [ ] **Step 2: Compile**

Run:

```powershell
mvn compile -DskipTests
```

Expected: `BUILD SUCCESS`.

---

## Task 3: Harden EventController and Event List Template

**Files:**
- Modify: `src/main/java/com/sportsmeet/controller/EventController.java`
- Modify: `src/main/resources/templates/event/list.html`

- [ ] **Step 1: Modify EventController imports**

Add imports:

```java
import com.sportsmeet.common.BusinessException;
import com.sportsmeet.common.SecurityValidator;
```

- [ ] **Step 2: Sanitize list filters**

Change the start of `list(...)` to:

```java
String safeCategory = SecurityValidator.validCategory(category, false);
String safeKeyword = SecurityValidator.cleanKeyword(keyword);
List<Event> events = eventService.findAll(safeCategory, safeKeyword);
```

Then use `safeCategory` and `safeKeyword` in model attributes.

- [ ] **Step 3: Add event validation helper**

Add private method inside `EventController`:

```java
private void validateEvent(Event event, boolean requireId) {
    if (requireId) {
        SecurityValidator.validId(event.getId(), "项目编号");
    }
    event.setName(SecurityValidator.validText(event.getName(), "项目名称", 50));
    event.setCategory(SecurityValidator.validCategory(event.getCategory(), true));
    event.setTopN(SecurityValidator.validTopN(event.getTopN()));
    event.setLocation(SecurityValidator.validText(event.getLocation(), "比赛地点", 50));
    if (event.getEventDate() == null) {
        throw new BusinessException("比赛日期不能为空");
    }
    if (event.getEventTime() == null) {
        throw new BusinessException("比赛时间不能为空");
    }
    if (requireId) {
        event.setStatus(SecurityValidator.validStatus(event.getStatus()));
    } else {
        event.setStatus(event.getStatus() == null ? 0 : SecurityValidator.validStatus(event.getStatus()));
    }
}
```

- [ ] **Step 4: Validate save/update/edit IDs**

In `save`, call:

```java
validateEvent(event, false);
```

In `update`, call:

```java
validateEvent(event, true);
if (eventService.findById(event.getId()) == null) {
    throw new BusinessException("比赛项目不存在");
}
```

In `editPage`, validate and check:

```java
SecurityValidator.validId(id, "项目编号");
Event event = eventService.findById(id);
if (event == null) {
    throw new BusinessException("比赛项目不存在");
}
model.addAttribute("event", event);
```

- [ ] **Step 5: Convert delete/finish to POST**

Replace:

```java
@GetMapping("/delete/{id}")
public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
```

with:

```java
@PostMapping("/delete")
public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
```

At the top of the method:

```java
SecurityValidator.validId(id, "项目编号");
if (eventService.findById(id) == null) {
    throw new BusinessException("比赛项目不存在");
}
```

Replace:

```java
@GetMapping("/finish/{id}")
public String finish(@PathVariable Long id, RedirectAttributes redirectAttributes) {
```

with:

```java
@PostMapping("/finish")
public String finish(@RequestParam Long id, RedirectAttributes redirectAttributes) {
```

At the top of the method:

```java
SecurityValidator.validId(id, "项目编号");
```

If event is null, throw `BusinessException`.

- [ ] **Step 6: Modify `event/list.html` finish/delete buttons to forms**

Replace the finish link with:

```html
<form th:if="${event.status != 2}" th:action="@{/event/finish}" method="post" class="d-inline" onsubmit="return confirm('确定要结束该比赛吗？结束后将不可再报名。')">
    <input type="hidden" name="id" th:value="${event.id}">
    <button type="submit" class="btn btn-sm btn-outline-warning me-1">
        <i class="fa-solid fa-flag-checkered"></i> 结束
    </button>
</form>
```

Replace the delete link with:

```html
<form th:action="@{/event/delete}" method="post" class="d-inline" onsubmit="return confirm('确定要删除该项目吗？此操作不可恢复。')">
    <input type="hidden" name="id" th:value="${event.id}">
    <button type="submit" class="btn btn-sm btn-outline-danger">
        <i class="fa-solid fa-trash-can"></i> 删除
    </button>
</form>
```

- [ ] **Step 7: Compile and test**

Run:

```powershell
mvn compile -DskipTests
```

Expected: `BUILD SUCCESS`.

Manual checks:

```text
/event/list?keyword=' OR 1=1 --
/event/delete/1
/event/finish/1
```

Expected: no data-changing action happens through GET; invalid keyword redirects with error.

---

## Task 4: Harden AthleteController and Athlete List Template

**Files:**
- Modify: `src/main/java/com/sportsmeet/controller/AthleteController.java`
- Modify: `src/main/resources/templates/athlete/list.html`

- [ ] **Step 1: Add imports**

```java
import com.sportsmeet.common.BusinessException;
import com.sportsmeet.common.SecurityValidator;
```

- [ ] **Step 2: Sanitize list filters**

At start of `list(...)`:

```java
Long safeDeptId = deptId == null ? null : SecurityValidator.validId(deptId, "院系编号");
String safeKeyword = SecurityValidator.cleanKeyword(keyword);
model.addAttribute("athletes", athleteService.findAll(safeDeptId, safeKeyword));
model.addAttribute("deptId", safeDeptId);
model.addAttribute("keyword", safeKeyword);
```

- [ ] **Step 3: Add validation helper**

Add private method:

```java
private void validateAthlete(Athlete athlete, boolean requireId) {
    if (requireId) {
        SecurityValidator.validId(athlete.getId(), "运动员编号");
    }
    athlete.setStudentNo(SecurityValidator.validText(athlete.getStudentNo(), "学号", 30));
    athlete.setName(SecurityValidator.validText(athlete.getName(), "姓名", 30));
    athlete.setGender(SecurityValidator.validGender(athlete.getGender()));
    athlete.setDeptId(SecurityValidator.validId(athlete.getDeptId(), "院系编号"));
    athlete.setPhone(SecurityValidator.validOptionalText(athlete.getPhone(), "联系电话", 20));
}
```

- [ ] **Step 4: Validate save/update/edit/delete**

In save:

```java
validateAthlete(athlete, false);
```

In update:

```java
validateAthlete(athlete, true);
if (athleteService.findById(athlete.getId()) == null) {
    throw new BusinessException("运动员不存在");
}
```

In editPage:

```java
SecurityValidator.validId(id, "运动员编号");
Athlete athlete = athleteService.findById(id);
if (athlete == null) {
    throw new BusinessException("运动员不存在");
}
model.addAttribute("athlete", athlete);
```

Change delete from GET path variable to POST request param:

```java
@PostMapping("/delete")
public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
    SecurityValidator.validId(id, "运动员编号");
    if (athleteService.findById(id) == null) {
        throw new BusinessException("运动员不存在");
    }
    athleteService.deleteById(id);
    redirectAttributes.addFlashAttribute("msg", "运动员删除成功");
    return "redirect:/athlete/list";
}
```

- [ ] **Step 5: Modify delete button in `athlete/list.html`**

Replace delete anchor with:

```html
<form th:action="@{/athlete/delete}" method="post" class="d-inline" onsubmit="return confirm('确定要删除该运动员吗？')">
    <input type="hidden" name="id" th:value="${athlete.id}">
    <button type="submit" class="btn btn-sm btn-outline-danger">
        <i class="fa-solid fa-trash-can"></i> 删除
    </button>
</form>
```

- [ ] **Step 6: Compile and test**

Run:

```powershell
mvn compile -DskipTests
```

Expected: `BUILD SUCCESS`.

Manual check:

```text
/athlete/delete/1
/athlete/edit/999999
/athlete/list?keyword=' OR 1=1 --
```

Expected: no Whitelabel, GET delete does not delete.

---

## Task 5: Harden RegistrationController and Template

**Files:**
- Modify: `src/main/java/com/sportsmeet/controller/RegistrationController.java`
- Modify: `src/main/resources/templates/registration/list.html`

- [ ] **Step 1: Add imports**

```java
import com.sportsmeet.common.SecurityValidator;
```

- [ ] **Step 2: Sanitize list filters**

At start of `list(...)`:

```java
Long safeEventId = eventId == null ? null : SecurityValidator.validId(eventId, "项目编号");
String safeKeyword = SecurityValidator.cleanKeyword(keyword);
model.addAttribute("registrations", registrationService.findAll(safeEventId, safeKeyword));
model.addAttribute("eventId", safeEventId);
model.addAttribute("keyword", safeKeyword);
```

- [ ] **Step 3: Validate register IDs**

At start of `register(...)`:

```java
SecurityValidator.validId(athleteId, "运动员编号");
SecurityValidator.validId(eventId, "项目编号");
```

- [ ] **Step 4: Convert cancel to POST**

Replace GET method with:

```java
@PostMapping("/cancel")
public String cancel(@RequestParam Long id, RedirectAttributes redirectAttributes) {
    SecurityValidator.validId(id, "报名编号");
    registrationService.cancel(id);
    redirectAttributes.addFlashAttribute("msg", "报名取消成功");
    return "redirect:/registration/list";
}
```

- [ ] **Step 5: Modify cancel button in `registration/list.html`**

Replace cancel anchor with:

```html
<form th:if="${reg.status == 0}" th:action="@{/registration/cancel}" method="post" class="d-inline" onsubmit="return confirm('确定要取消该报名吗？')">
    <input type="hidden" name="id" th:value="${reg.id}">
    <button type="submit" class="btn btn-sm btn-outline-warning" id="btn-cancel">
        <i class="fa-solid fa-ban"></i> 取消
    </button>
</form>
```

- [ ] **Step 6: Compile and test**

Run:

```powershell
mvn compile -DskipTests
```

Expected: `BUILD SUCCESS`.

Manual check:

```text
/registration/cancel/1
/registration/list?keyword=' OR 1=1 --
```

Expected: GET cancel does not cancel; invalid keyword redirects with error.

---

## Task 6: Harden ScoreController and ReportController

**Files:**
- Modify: `src/main/java/com/sportsmeet/controller/ScoreController.java`
- Modify: `src/main/java/com/sportsmeet/controller/ReportController.java`

- [ ] **Step 1: Add imports**

In both files:

```java
import com.sportsmeet.common.BusinessException;
import com.sportsmeet.common.SecurityValidator;
```

- [ ] **Step 2: Harden score inputForm/view event ID**

In `ScoreController.inputForm`:

```java
SecurityValidator.validId(eventId, "项目编号");
Event event = eventService.findById(eventId);
if (event == null) {
    throw new BusinessException("比赛项目不存在");
}
model.addAttribute("event", event);
```

In `ScoreController.view`:

```java
SecurityValidator.validId(eventId, "项目编号");
Event event = eventService.findById(eventId);
if (event == null) {
    throw new BusinessException("比赛项目不存在");
}
model.addAttribute("event", event);
```

- [ ] **Step 3: Validate score submit arrays**

At start of `submit(...)`:

```java
SecurityValidator.validId(eventId, "项目编号");
if (athleteIds == null || ranks == null || results == null || athleteIds.isEmpty()) {
    throw new BusinessException("没有可提交的成绩数据");
}
if (athleteIds.size() != ranks.size() || athleteIds.size() != results.size()) {
    throw new BusinessException("成绩数据不完整");
}
for (int i = 0; i < athleteIds.size(); i++) {
    SecurityValidator.validId(athleteIds.get(i), "运动员编号");
    SecurityValidator.validRank(ranks.get(i));
    results.set(i, SecurityValidator.validText(results.get(i), "成绩", 50));
}
```

- [ ] **Step 4: Harden ReportController**

In personal report:

```java
String safeKeyword = SecurityValidator.cleanKeyword(keyword);
model.addAttribute("keyword", safeKeyword);
if (safeKeyword != null) {
    model.addAttribute("scores", scoreService.findPersonalScores(safeKeyword));
}
```

In event report:

```java
Long safeEventId = eventId == null ? null : SecurityValidator.validId(eventId, "项目编号");
model.addAttribute("eventId", safeEventId);
if (safeEventId != null) {
    Event event = eventService.findById(safeEventId);
    if (event == null) {
        throw new BusinessException("比赛项目不存在");
    }
    model.addAttribute("event", event);
    model.addAttribute("scores", scoreService.findByEventId(safeEventId));
}
```

- [ ] **Step 5: Compile and test**

Run:

```powershell
mvn compile -DskipTests
```

Expected: `BUILD SUCCESS`.

Manual check:

```text
/score/input/999999
/score/view/999999
/report/personal?keyword=' OR 1=1 --
/report/event?eventId=999999
```

Expected: no Whitelabel; redirects with error messages.

---

## Task 7: Harden ApiController

**Files:**
- Modify: `src/main/java/com/sportsmeet/controller/ApiController.java`

- [ ] **Step 1: Add imports**

```java
import com.sportsmeet.common.BusinessException;
import com.sportsmeet.common.SecurityValidator;
```

- [ ] **Step 2: Validate login input**

At start of API login:

```java
username = SecurityValidator.validText(username, "用户名", 30);
password = SecurityValidator.validText(password, "密码", 50);
```

Wrap body in try/catch:

```java
try {
    username = SecurityValidator.validText(username, "用户名", 30);
    password = SecurityValidator.validText(password, "密码", 50);
    SysUser user = userService.login(username, password);
    if (user == null) {
        return Result.fail("用户名或密码错误");
    }
    session.setAttribute("loginUser", user);
    return Result.success(user);
} catch (BusinessException e) {
    return Result.fail(e.getMessage());
}
```

- [ ] **Step 3: Validate events params**

Wrap `/api/events` body:

```java
try {
    String safeCategory = SecurityValidator.validCategory(category, false);
    String safeKeyword = SecurityValidator.cleanKeyword(keyword);
    return Result.success(eventService.findAll(safeCategory, safeKeyword));
} catch (BusinessException e) {
    return Result.fail(e.getMessage());
}
```

- [ ] **Step 4: Compile and test API**

Run:

```powershell
mvn compile -DskipTests
```

Expected: `BUILD SUCCESS`.

Manual check:

```powershell
Invoke-WebRequest "http://localhost:8080/api/events?keyword=' OR 1=1 --" -UseBasicParsing
```

Expected: JSON fail response, not Whitelabel.

---

## Task 8: Full Verification and Commit

**Files:**
- Verify all changed files.

- [ ] **Step 1: Stop old server**

Run:

```powershell
$conns = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue; foreach ($c in $conns) { if ($c.OwningProcess -ne 0 -and $c.OwningProcess -ne 4) { taskkill /F /PID $c.OwningProcess } }
```

- [ ] **Step 2: Compile cleanly**

Run:

```powershell
mvn clean compile -DskipTests
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 3: Start server**

Run:

```powershell
mvn spring-boot:run
```

Expected: `Tomcat started on port 8080`.

- [ ] **Step 4: Manual browser test**

Test these flows:

```text
1. Login admin / 123456
2. /event/list search normal keyword
3. /event/list?keyword=' OR 1=1 -- shows error, not Whitelabel
4. Event finish button works via POST
5. Event delete button works via POST when allowed
6. /event/delete/1 does not delete
7. /athlete/list search works
8. /athlete/delete/1 does not delete through GET
9. /registration/list search works
10. /registration/cancel/1 does not cancel through GET
11. /score/input/999999 shows friendly error
12. /report/event?eventId=999999 shows friendly error
```

- [ ] **Step 5: Commit and push**

Run:

```powershell
git add -A
git commit -m "security: add input validation and POST-only dangerous actions"
git push origin master
```

Expected: GitHub repository updated.

---

## Self-Review

- Spec coverage: SQL-injection-style input hardening is covered by `SecurityValidator.cleanKeyword` and controller usage. URL tampering is covered by ID validation and resource existence checks. Unsafe GET actions are converted to POST in controllers and templates.
- Placeholder scan: No TBD/TODO placeholders remain.
- Type consistency: Helper method names are consistent: `validId`, `cleanKeyword`, `validCategory`, `validGender`, `validStatus`, `validTopN`, `validRank`, `validText`, `validOptionalText`.
