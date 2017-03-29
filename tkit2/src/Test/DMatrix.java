/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.HashMap;
import madkit.kernel.AgentAddress;

/**
 *
 * @author Ximo
 */
class DMatrix {
    HashMap<myBail, HashMap<myBail,Integer>> DMatrix = new HashMap();
    public void put(myBail mybail1, myBail mybail2, Integer value){
        updateHashMap(this.DMatrix,mybail1,mybail2,value);
  }
    public Integer get(myBail mybail1, myBail mybail2){
       return this.DMatrix.get(mybail1).get(mybail2);
    }
    private <T> void updateHashMap(HashMap<myBail, HashMap<myBail, T>> provisional, myBail bail1, myBail bail2, T number) {
                    HashMap<myBail,T> provisional2 = new HashMap();
                      try{
                      provisional2.putAll(provisional.get(bail1));
                      } catch (NullPointerException e){}
                      provisional2.put(bail2, number);
                      provisional.put(bail1, provisional2);
    }
}
