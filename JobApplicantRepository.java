package com.placement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.placement.entities.JobApplicant;
import com.placement.entities.Jobs;

@Repository
public interface JobApplicantRepository extends JpaRepository<JobApplicant, Long> {

	@Query(value = "SELECT * FROM job_applicant ja WHERE ja.sid = :sid",nativeQuery = true)
	public List<JobApplicant> getJobApplicantBySid( @Param("sid")long sid);
	
	@Query(value = "SELECT * FROM job_applicant ja WHERE ja.sid = :sid and ja.jid = :jid",nativeQuery = true)
	public List<JobApplicant> getJobApplicantBySidandRid( @Param("sid")long sid, @Param("jid")long jid);
	
}
