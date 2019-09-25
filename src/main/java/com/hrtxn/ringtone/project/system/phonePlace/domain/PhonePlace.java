package com.hrtxn.ringtone.project.system.phonePlace.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class PhonePlace {
    private Long id;

    private String phone;

    private String operator;

    private String province;

    private String city;

}