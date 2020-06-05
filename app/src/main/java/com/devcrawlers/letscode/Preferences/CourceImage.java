package com.devcrawlers.letscode.Preferences;

import android.content.Context;

import com.devcrawlers.letscode.R;
import com.devcrawlers.letscode.modeles.Course;

import java.util.Arrays;
import java.util.List;

public class CourceImage {
    public static int from(Context context, Course cource) {
        String[] stringArray = context.getResources().getStringArray(R.array.channels);
        List<String> strings = Arrays.asList(stringArray);

        if (cource.getChannel().contains("Algorithm"))
            return R.drawable.course_ic_algorithm;

        if (cource.getChannel().contains("Android"))
            return R.drawable.course_ic_android;

        if (cource.getChannel().contains("Asp"))
            return R.drawable.course_ic_asp;

        if (cource.getChannel().contains("Cpp"))
            return R.drawable.course_ic_cpp;

        if (cource.getChannel().contains("Cryptography"))
            return R.drawable.course_ic_cryptography;


        if (cource.getChannel().contains("Data Structure"))
            return R.drawable.course_ic_datastructures;


        if (cource.getChannel().contains(".Net"))
            return R.drawable.course_ic_dotnet;


        if (cource.getChannel().contains("Flutter"))
            return R.drawable.course_ic_flutter;


        if (cource.getChannel().contains("HTML"))
            return R.drawable.course_ic_html;


        if (cource.getChannel().contains("JavaScript"))
            return R.drawable.course_ic_javascript;


        if (cource.getChannel().contains("JQuery"))
            return R.drawable.course_ic_jquery;


        if (cource.getChannel().contains("Kotlin"))
            return R.drawable.course_ic_kotlin;


        if (cource.getChannel().contains("Language C"))
            return R.drawable.course_ic_language_c;


        if (cource.getChannel().contains("Laravel"))
            return R.drawable.course_ic_laravel;


        if (cource.getChannel().contains("Linux"))
            return R.drawable.course_ic_lunix;


        if (cource.getChannel().contains("MySQL"))
            return R.drawable.course_ic_mysql;


        if (cource.getChannel().contains("Networking"))
            return R.drawable.course_ic_networking;


        if (cource.getChannel().contains("PHP"))
            return R.drawable.course_ic_php;


        if (cource.getChannel().contains("Python"))
            return R.drawable.course_ic_python;

        if (cource.getChannel().contains("Raspberry PI"))
            return R.drawable.course_ic_raspberrypi;


        if (cource.getChannel().contains("React"))
            return R.drawable.course_ic_react;

        if (cource.getChannel().contains("Spring"))
            return R.drawable.course_ic_spring;

        if (cource.getChannel().contains("Swift"))
            return R.drawable.course_ic_swift;


        if (cource.getChannel().contains("Symfony"))
            return R.drawable.course_ic_symfony;


        if (cource.getChannel().contains("XML"))
            return R.drawable.course_ic_xml;

        return 0;
    }
}
