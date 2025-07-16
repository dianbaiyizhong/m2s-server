package com.nntk.m2s.service;

import java.util.List;

public interface IAiService {

    public String getDeepSeekResponse(String prompt);

    public String getBailianResponse(String prompt);

    public String getRawDeepSeekResponse(String prompt);

    public List<String> getBrowserUseResponse(List<String> prompt);

}
