package org.example;

import sun.reflect.generics.tree.Tree;

import java.io.*;
import java.util.*;

class CompFirstName implements Comparator<String> {

    @Override
    public int compare(String aStr, String bStr) {
        return aStr.substring(0,1).compareToIgnoreCase(bStr.substring(0,1));
    }
}

class CompLastNum implements Comparator<String> {

    StringBuffer sb = new StringBuffer();

    @Override
    public int compare(String aStr, String bStr) {
        String a = String.format("%5s", aStr).replace(" ", "0");
        String b = String.format("%5s", bStr).replace(" ", "0");
        return a.compareToIgnoreCase(b);
    }
}

public class HW1 {
    double cosine (){
        return 0.0;
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.printf("파일 이름, target 사용자, 참고인 수, 항목 수? ");
        String file = sc.next();
        int target = sc.nextInt();
        int compare = sc.nextInt();
        int item = sc.nextInt();


        BufferedReader reader = new BufferedReader(new FileReader(file));

        CompFirstName compFN = new CompFirstName();
        Comparator<String> compLastThenFirst = compFN.thenComparing(new CompLastNum());
        HashMap<Integer, TreeMap<String, Double>> doubleHash = new HashMap<Integer, TreeMap<String, Double>>();
        String str;
        int num;
        num = Integer.parseInt(reader.readLine());
        while ((str = reader.readLine()) != null ){
            //System.out.println(str);
            String[] str2 = str.split(" ");
            if(doubleHash.get(Integer.parseInt(str2[0])) == null){
                TreeMap<String, Double> map = new TreeMap<String, Double>(compLastThenFirst);
                map.put(str2[1], Double.parseDouble(str2[2]));
                doubleHash.put(Integer.parseInt(str2[0]), map);
            } else {
                doubleHash.get(Integer.parseInt(str2[0])).put(str2[1], Double.parseDouble(str2[2]));
            }

        }


        TreeMap<String, Double> map = new TreeMap<String, Double>(compLastThenFirst);

        for( int i : doubleHash.keySet()) {
            map = doubleHash.get(i);
            double sum = 0;
            int n = 0;
            for (double j : map.values()){
                sum += j;
                n++;
            }

            double avg = sum / n ;

            for (String contents : map.keySet()){
                map.put(contents ,map.get(contents) - avg);
            }
        } // 정규화 완료


        int size = doubleHash.get(target).keySet().size();
        int n = 0;
        System.out.printf("1. 사용자 %d의 콘텐츠와 정규화 점수 : \n[",target);
        for(String s : doubleHash.get(target).keySet()){
            n++;
            if (n == size) {
                System.out.printf("(%s, %.3f)]\n",s, Math.round(1000*doubleHash.get(target).get(s))/ 1000.0);
                break;
            }
            System.out.printf("(%s, %.3f), ",s, Math.round(1000*doubleHash.get(target).get(s))/ 1000.0);

        }

        System.out.println(doubleHash.get(5).entrySet());

    }
}
