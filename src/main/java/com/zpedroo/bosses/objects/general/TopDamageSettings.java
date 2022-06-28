package com.zpedroo.bosses.objects.general;

import lombok.Data;

import java.util.List;

@Data
public class TopDamageSettings {

    private final String display;
    private final List<String> commands;
}