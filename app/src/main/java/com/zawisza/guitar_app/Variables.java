package com.zawisza.guitar_app;

import android.widget.Button;
import android.widget.TextView;

public class Variables {

    static Button button_add;
    static Button button_login;
    static TextView titleTextView;
    static TextView backTextView;

    private static boolean isContent;

    private static String topic_to_android = "announcements_android";
    private static String topic_to_ios = "announcements_ios";
    private static String announcements = "Announcements";
    private static String Timetables = "Timetables";
    private static String FAQs = "FAQs";
    private static String Enrollments = "Enrollments";
    private static String Routes = "Routes";
    private static String Images = "Images";
    private static String Emails = "Emails";
    private static String Links = "Links";



    private static int icon_user = R.drawable.person_profile_image_icon;
    private static int icon_back = R.drawable.back_icon;
    private static int icon_add = R.drawable.plus_round_line_icon_3;


    public static String getTopic_to_android() {
        return topic_to_android;
    }

    public static int getIcon_user() {
        return icon_user;
    }

    public static int getIcon_back() {
        return icon_back;
    }

    public static String getAnnouncements() {
        return announcements;
    }

    public static String getTimetables() {
        return Timetables;
    }

    public static String getFAQs() {
        return FAQs;
    }

    public static String getEnrollments() {
        return Enrollments;
    }

    public static String getTopic_to_ios() {
        return topic_to_ios;
    }

    public static String getRoutes() {
        return Routes;
    }

    public static String getImages(){ return Images;}

    public static Button getButton_add() {
        return button_add;
    }
    public static void setButton_add(Button button_add) {
        Variables.button_add = button_add;
    }

    public static Button getButton_login() {
        return button_login;
    }
    public static void setButton_login(Button button_login) {
        Variables.button_login = button_login;
    }

    public static TextView getTitleTextView() {
        return titleTextView;
    }
    public static void setTitleTextView(TextView titleTextView) {
        Variables.titleTextView = titleTextView;
    }

    public static TextView getBackTextView() {
        return backTextView;
    }
    public static void setBackTextView(TextView backTextView) {
        Variables.backTextView = backTextView;
    }

    public static boolean isIsContent() {
        return isContent;
    }
    public static void setIsContent(boolean isContent) {
        Variables.isContent = isContent;
    }

    public static String getEmails() {
        return Emails;
    }
    public static void setEmails(String emails) {
        Emails = emails;
    }

    public static String getLinks() {
        return Links;
    }
}
