package com.college.cms.dto;

public class ResultResponseDTO {
    private Long result_id;
    private String grade;
    private String status;
    private Integer totalMarks;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String studentRollNo;
    private Long userId;
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private Integer courseId;
    private String courseName;
    private Integer staffId;
    private String staffName;

    public ResultResponseDTO() {}

    public ResultResponseDTO(Long result_id, String grade, String status, Integer totalMarks,
                             Long studentId, String studentName, Long subjectId, String subjectName) {
        this.result_id = result_id;
        this.grade = grade;
        this.status = status;
        this.totalMarks = totalMarks;
        this.studentId = studentId;
        this.studentName = studentName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }

    public ResultResponseDTO(Long result_id, String grade, String status, Integer totalMarks,
                             Long studentId, String studentName, String studentEmail, String studentRollNo,
                             Long userId, Long subjectId, String subjectName, String subjectCode) {
        this.result_id = result_id;
        this.grade = grade;
        this.status = status;
        this.totalMarks = totalMarks;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentRollNo = studentRollNo;
        this.userId = userId;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }

    public ResultResponseDTO(Long result_id, String grade, String status, Integer totalMarks,
                             Long studentId, String studentName, String studentEmail, String studentRollNo,
                             Long userId, Long subjectId, String subjectName, String subjectCode,
                             Integer courseId, String courseName, Integer staffId, String staffName) {
        this.result_id = result_id;
        this.grade = grade;
        this.status = status;
        this.totalMarks = totalMarks;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentRollNo = studentRollNo;
        this.userId = userId;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.courseId = courseId;
        this.courseName = courseName;
        this.staffId = staffId;
        this.staffName = staffName;
    }

    // --- Getters & Setters ---
    public Long getResult_id() { return result_id; }
    public void setResult_id(Long result_id) { this.result_id = result_id; }

    public Long getResultId() { return result_id; }
    public void setResultId(Long resultId) { this.result_id = resultId; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Integer totalMarks) { this.totalMarks = totalMarks; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getStudentRollNo() { return studentRollNo; }
    public void setStudentRollNo(String studentRollNo) { this.studentRollNo = studentRollNo; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
}