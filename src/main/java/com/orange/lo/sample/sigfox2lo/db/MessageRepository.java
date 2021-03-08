package com.orange.lo.sample.sigfox2lo.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	
	@Modifying(flushAutomatically = true)
	@Query("delete Message m where m.time < :time")
	public int removeByTimeLessThan(@Param("time")Long epochSeconds);
}
