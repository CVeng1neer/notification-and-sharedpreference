package com.example.rememberpassword;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

//创建一个活动管理器，用于管理所有的活动（添加、删除、结束）
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<Activity>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishAll(){
        for (Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}