/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.HashMap;

/**
 *
 * @author Ximo
 */
class TMatrix {
    HashMap<myBail, HashMap<myBail,Double>> TMatrix = new HashMap();
    public void put(myBail mybail1, myBail mybail2, Double value){
        updateHashMap(this.TMatrix,mybail1,mybail2,value);
  }
    public Double get(myBail mybail1, myBail mybail2){
       return this.TMatrix.get(mybail1).get(mybail2);
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
