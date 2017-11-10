package com.stonymoon.bubble.bean;

import java.io.File;
import java.util.Map;

import cn.jpush.im.android.api.callback.DownloadAvatarCallback;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;


public class JUserBean extends UserInfo {

    @Override
    public String getAppKey() {
        return null;
    }

    @Override
    public String getNoteText() {
        return null;
    }

    @Override
    public void setNoDisturb(int i, BasicCallback basicCallback) {

    }

    @Override
    public int getBlacklist() {
        return 0;
    }

    @Override
    public void getAvatarBitmap(GetAvatarBitmapCallback getAvatarBitmapCallback) {

    }

    @Override
    public long getBirthday() {
        return 0;
    }

    @Override
    public void setBirthday(long l) {

    }

    @Override
    public boolean isFriend() {
        return false;
    }

    @Override
    public void updateNoteName(String s, BasicCallback basicCallback) {

    }

    @Override
    public void updateNoteText(String s, BasicCallback basicCallback) {

    }

    @Override
    public File getAvatarFile() {
        return null;
    }

    @Override
    public File getBigAvatarFile() {
        return null;
    }

    @Override
    public void removeFromFriendList(BasicCallback basicCallback) {

    }

    @Override
    public void getAvatarFileAsync(DownloadAvatarCallback downloadAvatarCallback) {

    }


    @Override
    public String getDisplayName() {
        return null;
    }


    @Override
    public void getBigAvatarBitmap(GetAvatarBitmapCallback getAvatarBitmapCallback) {

    }

    @Override
    public void setUserExtras(Map<String, String> map) {


    }

    @Override
    public void setUserExtras(String s, String s1) {
        extras.put(s, s1);



    }

    @Override
    public String getNotename() {
        return null;
    }

    @Override
    public int getNoDisturb() {
        return 0;
    }
}
