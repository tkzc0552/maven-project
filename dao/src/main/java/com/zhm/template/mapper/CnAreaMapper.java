package com.zhm.template.mapper;

import com.zhm.template.entity.CnArea;
import com.zhm.template.entity.CnAreaExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CnAreaMapper {
    long countByExample(CnAreaExample example);

    int deleteByExample(CnAreaExample example);

    int insert(CnArea record);

    int insertSelective(CnArea record);

    List<CnArea> selectByExample(CnAreaExample example);

    int updateByExampleSelective(@Param("record") CnArea record, @Param("example") CnAreaExample example);

    int updateByExample(@Param("record") CnArea record, @Param("example") CnAreaExample example);
}