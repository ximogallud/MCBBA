/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trueTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class AllCombinations {
    public static void main(String[] args) {
        
        
        List<Integer> list = Arrays.asList(1,2,4,5,6,7);

        List<Set<Integer>> subset = getSubsets( list, 3);
        int j = 0;
        HashMap<Integer,ArrayList<Integer>> solution = new HashMap();
        for (int k = 0; k < subset.size(); k++){
           ArrayList<Integer> subset1 = new ArrayList(subset.get(k));
           ArrayList<ArrayList<Integer>> anotherArray = new ArrayList();
           ArrayList<ArrayList<Integer>> myArray = new ArrayList();
           myArray = permute(subset1, subset1.size(), anotherArray);
           for (int i = 0; i < myArray.size(); i++){
               solution.put(j, myArray.get(i));
               j++;
           }
        }
    }
    public static <T> List<Set<T>> getSubsets(List<T> superSet, int k) {
    List<Set<T>> res = new ArrayList<>();
    getSubsets(superSet, k, 0, new HashSet<T>(), res);
    return res;
}
    private static <T> void getSubsets(List<T> superSet, int k, int idx, Set<T> current,List<Set<T>> solution) {
    //successful stop clause
    if (current.size() == k) {
        solution.add(new HashSet<>(current));
        return;
    }
    //unseccessful stop clause
    if (idx == superSet.size()) return;
    T x = superSet.get(idx);
    current.add(x);
    //"guess" x is in the subset
    getSubsets(superSet, k, idx+1, current, solution);
    current.remove(x);
    //"guess" x is not in the subset
    getSubsets(superSet, k, idx+1, current, solution);
}

   static <T> ArrayList<ArrayList<T>> permute(ArrayList<T> arr, int k, ArrayList<ArrayList<T>> solution){
        if (k == 1){
            ArrayList<T> auxiliary = new ArrayList(arr);
            solution.add(auxiliary);
        } else {
            for(int i = 0; i < k ; i++){
            permute(arr,k-1, solution);
            if (k % 2 == 1){
                swap (arr, 0, k - 1);
            } else {
                 swap(arr, i, k - 1);
            }
           
        }
        }
        return solution;
    }
    private static <T> void swap(ArrayList<T> setBails, int k, int i) {
        T t;
        t = setBails.get(k);
        setBails.set(k, setBails.get(i));
        setBails.set(i, t);
    }
}