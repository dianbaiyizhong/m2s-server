package com.nntk.m2s;


import com.google.common.collect.Lists;
import com.nntk.m2s.mp.generate.mapper.TCountryMapper;
import com.nntk.m2s.service.impl.GeoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MyTest {


    @Mock
    private TCountryMapper countryMapper;
    @InjectMocks
    private GeoServiceImpl geoServiceImpl;

    @Test
    void testGetUserById_Success() {

        // 模拟repository行为
        when(countryMapper.selectList(any()))
                .thenReturn(Lists.newArrayList());


        System.out.println("ww" + geoServiceImpl.getCountryInfo());

    }
}
