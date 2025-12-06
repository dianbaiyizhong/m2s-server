package com.nntk.m2s.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class GameScheduleInfo  implements Serializable {

    private String time;
    private String guestTeam;
    private String homeTeam;
    private String homeRate;
    private String guestRate;

    private String competitor;


}
