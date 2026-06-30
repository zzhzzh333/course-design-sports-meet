package com.sportsmeet.entity;

import java.time.LocalDateTime;

public class Score {
    private Long id;
    private Long registrationId;
    private Long athleteId;
    private Long eventId;
    private Integer rank;
    private String result;
    private Integer points;
    private LocalDateTime createTime;

    private String athleteName;
    private String deptName;
    private String eventName;
    private String athleteStudentNo;
    private String eventCategory;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }
    public Long getAthleteId() { return athleteId; }
    public void setAthleteId(Long athleteId) { this.athleteId = athleteId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getAthleteName() { return athleteName; }
    public void setAthleteName(String athleteName) { this.athleteName = athleteName; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getAthleteStudentNo() { return athleteStudentNo; }
    public void setAthleteStudentNo(String athleteStudentNo) { this.athleteStudentNo = athleteStudentNo; }
    public String getEventCategory() { return eventCategory; }
    public void setEventCategory(String eventCategory) { this.eventCategory = eventCategory; }
}