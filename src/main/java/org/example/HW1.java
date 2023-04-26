package org.example;

import java.io.*;
import java.util.*;

class CompFirstName implements Comparator<String> {

    @Override
    public int compare(String aStr, String bStr) {
        return aStr.substring(0,1).compareToIgnoreCase(bStr.substring(0,1));
    }
}

class CompLastNum implements Comparator<String> {

    @Override
    public int compare(String aStr, String bStr) {
        String a = String.format("%5s", aStr).replace(" ", "0");
        String b = String.format("%5s", bStr).replace(" ", "0");
        return a.compareToIgnoreCase(b);
    }
}


public class HW1 {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.printf("파일 이름, target 사용자, 참고인 수, 항목 수? ");
        String file = sc.next();
        int target = sc.nextInt();
        int n = sc.nextInt();
        int k = sc.nextInt();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        HashMap<Integer, HashMap<String, Double>> doubleHash = new HashMap<Integer, HashMap<String, Double>>();

        String str;
        int num;
        long before_time = System.currentTimeMillis();
        num = Integer.parseInt(reader.readLine());
        while ((str = reader.readLine()) != null ){
            String[] str2 = str.split(" ");
            if(doubleHash.get(Integer.parseInt(str2[0])) == null){
                HashMap<String, Double> map = new HashMap<String, Double>();
                map.put(str2[1], Double.parseDouble(str2[2]));
                doubleHash.put(Integer.parseInt(str2[0]), map);
            } else {
                doubleHash.get(Integer.parseInt(str2[0])).put(str2[1], Double.parseDouble(str2[2]));
            }
        }

        HashMap<String, Double> map = new HashMap<String, Double>();
        for( int i : doubleHash.keySet()) {
            map = doubleHash.get(i);
            double sum = 0;
            num = 0;
            for (double j : map.values()){
                sum += j;
                num++;
            }
            double avg = sum / num ;

            for (String contents : map.keySet()){
                map.put(contents ,map.get(contents) - avg);
            }
        }

        int size = doubleHash.get(target).keySet().size();
        num = 0;
        System.out.println();
        System.out.printf("1. 사용자 %d의 콘텐츠와 정규화 점수 : \n[",target);
        ArrayList<String> content = new ArrayList<String>(doubleHash.get(target).keySet());

        for(String s : sort_content(content)){
            num++;
            if (num == size) {
                System.out.printf("(%s, %.3f)]\n\n",s, Math.round(1000*doubleHash.get(target).get(s))/ 1000.0);
                break;
            }
            System.out.printf("(%s, %.3f), ",s, Math.round(1000*doubleHash.get(target).get(s))/ 1000.0);

        }

        System.out.printf("2. 유사한 사용자 id와 유사도 리스트\n");

        HashMap<Integer, Double> similarity = new HashMap<Integer, Double>();

        for (int i : doubleHash.keySet()){

            similarity.put(i, cosine_similarity(doubleHash.get(target), doubleHash.get(i)));

        }

        similarity.remove(target);
        ArrayList<Integer> sim_keySet = new ArrayList<Integer>(similarity.keySet());
        sim_keySet.remove(target);
        sim_keySet.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return similarity.get(o2).compareTo(similarity.get(o1));
            }
        });

        int sim_num = 0;
        for (Integer key : sim_keySet) {
            if(sim_num == n){
                break;
            }
            System.out.print("사용자 id : " + key);
            System.out.printf(", 유사도 : %.6f\n",similarity.get(key));
            sim_num++;
        }

        System.out.println();
        System.out.printf("3. 사용자 %d에게 추천할 콘텐츠와 추천 점수\n[",target);

        HashSet<String> targetContent = new HashSet<String>(doubleHash.get(target).keySet());
        HashMap<String, Double> compContent = new HashMap<String, Double>();

        sim_num = 0;
        for(Integer key : sim_keySet) {
            if(sim_num == n){
                break;
            }
            HashSet<String> compTemp = new HashSet<String>(doubleHash.get(key).keySet());

            compTemp.removeAll(targetContent);

            for(String itr: compTemp){
                double score = doubleHash.get(key).get(itr) * similarity.get(key);

                if(compContent.containsKey(itr)){

                    if(compContent.get(itr) < score ) {
                        compContent.put(itr, compContent.get(itr)+score);
                    }
                }else {
                    compContent.put(itr, score);
                }
            }
            sim_num ++;
        }

        num = 0;
        for(String s : sort_score(compContent)){
            num++;
            if (num == k) {
                System.out.printf("(%s, %.3f)]\n\n",s, Math.round(1000*compContent.get(s))/ 1000.0);
                break;
            }
            System.out.printf("(%s, %.3f), ",s, Math.round(1000*compContent.get(s))/ 1000.0);

        }
        long after_time = System.currentTimeMillis();
        long secDifference = (after_time-before_time);
        System.out.printf("걸린시간(ms): "+secDifference);

    }
    static double cosine_similarity(HashMap<String, Double> user1, HashMap<String, Double> user2){
        double sum = 0;
        double sum_user1 = 0;
        double sum_user2 = 0;

        for(String content : user1.keySet()){
            if(user2.keySet().contains(content)){
                sum += user1.get(content) * user2.get(content);
            }
        }

        for(double val : user1.values()){
            sum_user1 += Math.pow(val, 2);
        }

        for(double val : user2.values()){
            sum_user2 += Math.pow(val, 2);
        }

        sum_user1 = Math.sqrt(sum_user1);
        sum_user2 = Math.sqrt(sum_user2);

        return (Double.isNaN(sum/(sum_user1 * sum_user2))) ? 0.0 :sum/(sum_user1 * sum_user2) ;
    }

    static ArrayList<String> sort_content (ArrayList<String> str) {
        ArrayList<String> sort_str = new ArrayList<String>();
        for(String s : str){
            sort_str.add(s);
        }

        CompFirstName compFN = new CompFirstName();
        Comparator<String> compLastThenFirst = compFN.thenComparing(new CompLastNum());

        sort_str.sort(compLastThenFirst);
        return sort_str;
    }

    static ArrayList<String> sort_score (HashMap<String, Double> contentScore) {
        ArrayList<String> sort_str = new ArrayList<String>();
        for(String s : contentScore.keySet()){
            sort_str.add(s);
        }

        Comparator<String> compFS = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                double s1 = (Math.round(1000*contentScore.get(o2))/ 1000.0);
                double s2 = (Math.round(1000*contentScore.get(o1))/ 1000.0);

                if (s1 == s2) {
                    return 0;
                } else {
                    return contentScore.get(o2).compareTo(contentScore.get(o1));
                }
            }
        };
        CompFirstName compFN = new CompFirstName();
        Comparator<String> compLastThenFirst = compFN.thenComparing(new CompLastNum());
        Comparator<String> compScoreThenOther = compFS.thenComparing(compLastThenFirst);

        sort_str.sort(compScoreThenOther);
        return sort_str;
    }
}
