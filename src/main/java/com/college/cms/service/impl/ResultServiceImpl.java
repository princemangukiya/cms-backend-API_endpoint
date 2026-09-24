package com.college.cms.service.impl;

import com.college.cms.dto.ResultResponseDTO;
import com.college.cms.entity.*;
import com.college.cms.repository.*;
import com.college.cms.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResultServiceImpl implements ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public void saveResult(Result result) {
        if (result.getStudent() == null || result.getStudent().getStudent_id() == null) {
            throw new RuntimeException("Student ID is missing");
        }
        if (result.getSubject() == null || result.getSubject().getSubject_id() == null) {
            throw new RuntimeException("Subject ID is missing");
        }

        Long sId = result.getStudent().getStudent_id();
        Long subId = result.getSubject().getSubject_id();

        Student student = studentRepository.findById(sId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + sId));

        Subject subject = subjectRepository.findById(subId)
                .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + subId));

        // Auto-calculate grade and status from total_marks if missing or empty
        String calculatedGrade = result.getGrade();
        String calculatedStatus = result.getStatus();
        if (result.getTotal_marks() != null) {
            int marks = result.getTotal_marks();
            if (calculatedGrade == null || calculatedGrade.trim().isEmpty()) {
                if (marks >= 90) calculatedGrade = "A+";
                else if (marks >= 80) calculatedGrade = "A";
                else if (marks >= 70) calculatedGrade = "B+";
                else if (marks >= 60) calculatedGrade = "B";
                else if (marks >= 40) calculatedGrade = "C";
                else calculatedGrade = "F";
            }
            if (calculatedStatus == null || calculatedStatus.trim().isEmpty()) {
                calculatedStatus = marks >= 40 ? "Pass" : "Fail";
            }
        }

        // Check if an existing result already exists for this Student & Subject
        // Real-world college logic: A student has one active exam record per subject!
        // When new marks are submitted, update the existing result instead of creating duplicates.
        List<Result> existingList = resultRepository.findByStudentIdAndSubjectId(sId, subId);
        Result targetResult;
        if (existingList != null && !existingList.isEmpty()) {
            targetResult = existingList.get(0);
            targetResult.setTotal_marks(result.getTotal_marks());
            targetResult.setGrade(calculatedGrade);
            targetResult.setStatus(calculatedStatus);

            // Clean up any other redundant duplicate rows from past tests
            if (existingList.size() > 1) {
                for (int i = 1; i < existingList.size(); i++) {
                    resultRepository.delete(existingList.get(i));
                }
            }
        } else {
            targetResult = result;
            targetResult.setStudent(student);
            targetResult.setSubject(subject);
            targetResult.setGrade(calculatedGrade);
            targetResult.setStatus(calculatedStatus);
        }

        resultRepository.save(targetResult);
    }

    @Override
    public List<ResultResponseDTO> getAllResultsDTO() {
        java.util.Map<Integer, String> courseMap = new java.util.HashMap<>();
        try {
            for (Course c : courseRepository.findAll()) {
                if (c.getCourseId() != null && c.getCourseName() != null) {
                    courseMap.put(c.getCourseId(), c.getCourseName());
                }
            }
        } catch (Exception ignored) {}

        java.util.Map<Integer, String> staffMap = new java.util.HashMap<>();
        try {
            for (Staff st : staffRepository.findAll()) {
                if (st.getStaffid() != null && st.getStaffname() != null) {
                    staffMap.put(st.getStaffid(), st.getStaffname());
                }
            }
        } catch (Exception ignored) {}

        List<Result> results = resultRepository.findAll();

        // 1. Sort newest first (highest result_id first)
        List<Result> sortedResults = results.stream()
                .sorted((a, b) -> Long.compare(
                        b.getResult_id() != null ? b.getResult_id() : 0L,
                        a.getResult_id() != null ? a.getResult_id() : 0L
                ))
                .collect(Collectors.toList());

        // 2. Deduplicate: keep only the newest record per student + subject
        java.util.Set<String> seenStudentSubjectKeys = new java.util.HashSet<>();
        List<Result> deduplicatedResults = new java.util.ArrayList<>();
        for (Result r : sortedResults) {
            Long sId = r.getStudent() != null ? r.getStudent().getStudent_id() : null;
            Long subId = r.getSubject() != null ? r.getSubject().getSubject_id() : null;
            if (sId != null && subId != null) {
                String key = sId + "_" + subId;
                if (!seenStudentSubjectKeys.contains(key)) {
                    seenStudentSubjectKeys.add(key);
                    deduplicatedResults.add(r);
                }
            } else {
                deduplicatedResults.add(r);
            }
        }

        return deduplicatedResults.stream()
                .map(r -> {
                    Integer cId = null;
                    if (r.getSubject() != null && r.getSubject().getCourseId() != null) {
                        cId = r.getSubject().getCourseId();
                    } else if (r.getStudent() != null && r.getStudent().getCourse_id() != null) {
                        cId = r.getStudent().getCourse_id();
                    }

                    String cName = cId != null && courseMap.containsKey(cId) ? courseMap.get(cId) : (cId != null ? "Course #" + cId : null);

                    Integer stId = r.getSubject() != null ? r.getSubject().getStaffId() : null;
                    String stName = stId != null && staffMap.containsKey(stId) ? staffMap.get(stId) : null;

                    return new ResultResponseDTO(
                            r.getResult_id(),
                            r.getGrade(),
                            r.getStatus(),
                            r.getTotal_marks(),
                            r.getStudent() != null ? r.getStudent().getStudent_id() : null,
                            r.getStudent() != null ? r.getStudent().getStudent_name() : "Unknown",
                            r.getStudent() != null ? r.getStudent().getEmail() : null,
                            r.getStudent() != null ? r.getStudent().getRoll_no() : null,
                            r.getStudent() != null && r.getStudent().getUser_id() != null ? r.getStudent().getUser_id().longValue() : null,
                            r.getSubject() != null ? r.getSubject().getSubject_id() : null,
                            r.getSubject() != null ? r.getSubject().getSubjectName() : "Unknown",
                            r.getSubject() != null ? r.getSubject().getSubjectCode() : null,
                            cId,
                            cName,
                            stId,
                            stName
                    );
                }).collect(Collectors.toList());
    }
}