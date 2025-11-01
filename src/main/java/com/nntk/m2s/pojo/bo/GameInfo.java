package com.nntk.m2s.pojo.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameInfo {

    private String homeTeam;
    private String guestTeam;

    private String homeRate;
    private String guestRate;




}
