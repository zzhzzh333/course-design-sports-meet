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