package com.shoogisoft.oneappleaday.common;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.SparseArray;
import android.widget.AbsListView;
  
  /**
   * Helper class for calculating relative scroll offsets in a ListView or GridView by tracking the
   * position of child views.
   */
  public class ListViewScrollTracker {
      private AbsListView mListView;
      private HashMap<Integer, Integer> mPositions;
  
      public ListViewScrollTracker(final AbsListView listView){
          mListView = listView;
      }
  
      /**
       * Call from an AbsListView.OnScrollListener to calculate the incremental offset (change in scroll offset
       * since the last calculation).
       *
       * @param firstVisiblePosition First visible item position in the list.
       * @param visibleItemCount Number of visible items in the list.
       * @return The incremental offset, or 0 if it wasn't possible to calculate the offset.
       */
      public int calculateIncrementalOffset(final int firstVisiblePosition, final int visibleItemCount){
          // Remember previous positions, if any
//          SparseArray<Integer> previousPositions = mPositions;
  
          // Store new positions
          if (mPositions == null)
        	  mPositions = new HashMap<Integer,Integer>();
          Integer currentPositionOffset;
//          if(mPositions.containsKey(firstVisiblePosition))
//          {
////        	  currentPositionOffset = mPositions.get(firstVisiblePosition);
//        	  
//        	  
//          }
//          else
//          {
//        	  currentPositionOffset = mListView.getChildAt(firstVisiblePosition).getTop();
//        	  mPositions.put(firstVisiblePosition, currentPositionOffset);
//          }
          if (mListView.getChildAt(firstVisiblePosition) == null)
        	  return 0;
          mPositions.put(firstVisiblePosition, mListView.getChildAt(firstVisiblePosition).getTop());
          
        	 
          int offset=0;
          Iterator it = mPositions.entrySet().iterator();
          while (it.hasNext()) {
              Map.Entry pairs = (Map.Entry)it.next();
              int key = ((Integer)pairs.getKey());
              if (key <= firstVisiblePosition)
              {
            	  offset += ((Integer)pairs.getValue());            	  
              }
              
//              it.remove(); // avoids a ConcurrentModificationException
          }
//          for(int i = 0; i < visibleItemCount; i++){
//              mPositions.put(firstVisiblePosition + i, mListView.getChildAt(i).getTop());
//          }
  
//          if(previousPositions != null){
//              // Find position which exists in both mPositions and previousPositions, then return the difference
//              // of the new and old Y values.
//              for(int i = 0; i < previousPositions.size(); i++){
//                  int position = previousPositions.keyAt(i);
//                  int previousTop = previousPositions.get(position);
//                  Integer newTop = mPositions.get(position);
//                  if(newTop != null){
//                      return newTop - previousTop;
//                  }
//              }
//          }
  
          return offset; // No view's position was in both previousPositions and mPositions
      }
  
      public void clear(){
          mPositions = null;
      }
  }