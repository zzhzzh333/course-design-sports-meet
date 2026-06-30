package com.sportsmeet.entity;

import java.time.LocalDateTime;

public class Registration {
    private Long id;
    private Long athleteId;
    private Long eventId;
    private LocalDateTime registerTime;
    private Integer status;
    private LocalDateTime createTime;

    private String athleteName;
    private String athleteStudentNo;
    private String deptName;
    private String eventName;
    private String eventCategory;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAthleteId() { return athleteId; }
    public void setAthleteId(Long athleteId) { this.athleteId = athleteId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public LocalDateTime getRegisterTime() { return registerTime; }
    public void setRegisterTime(LocalDateTime registerTime) { this.registerTime = registerTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getAthleteName() { return athleteName; }
    public void setAthleteName(String athleteName) { this.athleteName = athleteName; }
    public String getAthleteStudentNo() { return athleteStudentNo; }
    public void setAthleteStudentNo(String athleteStudentNo) { this.athleteStudentNo = athleteStudentNo; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getEventCategory() { return eventCategory; }
    public void setEventCategory(String eventCategory) { this.eventCategory = eventCategory; }
}