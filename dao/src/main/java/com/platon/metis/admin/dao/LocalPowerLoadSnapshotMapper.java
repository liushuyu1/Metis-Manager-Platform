package com.platon.metis.admin.dao;

import com.platon.metis.admin.dao.entity.LocalPowerLoadSnapshot;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LocalPowerLoadSnapshotMapper {
    List<LocalPowerLoadSnapshot> listLocalPowerLoadSnapshotByPowerNodeId(@Param("powerNodeId") String powerNodeId, @Param("hours") Integer hours);
}
