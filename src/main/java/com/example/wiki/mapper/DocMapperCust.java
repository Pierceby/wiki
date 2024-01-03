package com.example.wiki.mapper;

import com.example.wiki.domain.Test;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DocMapperCust {
    void increaseViewCount(Long id);

    void increaseVoteCount(Long id);

    void updateEbookInfo();

}
