package com.iguigui.qqbot.dto.neteasecloudmusic;

import java.util.List;

public class SongDetail {

    private List<Privileges> privileges;
    private int code;
    private List<Songs> songs;
    public void setPrivileges(List<Privileges> privileges) {
        this.privileges = privileges;
    }
    public List<Privileges> getPrivileges() {
        return privileges;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setSongs(List<Songs> songs) {
        this.songs = songs;
    }
    public List<Songs> getSongs() {
        return songs;
    }

}