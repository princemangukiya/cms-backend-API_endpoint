package com.college.cms.repository;

import com.college.cms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByAddedBy(String addedBy);

    List<Attendance> findByUserid(Integer userid);

    // Apni khud ki attendance + apne dwara bhari gayi attendance
    @Query("SELECT a FROM Attendance a WHERE a.userid = :userid OR a.addedBy = :addedBy ORDER BY a.attendancedate DESC, a.attendanceid DESC")
    List<Attendance> findByUseridOrAddedBy(@Param("userid") Integer userid, @Param("addedBy") String addedBy);
}