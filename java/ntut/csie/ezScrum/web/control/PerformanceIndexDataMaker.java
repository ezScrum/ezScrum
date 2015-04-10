/**
 * Performance Index includes SPI and CPI
 * **/
package ntut.csie.ezScrum.web.control;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;

public class PerformanceIndexDataMaker {
	
//	List<SprintBacklogMapper> sprintBacklogList;
//	List<ISprintPlanDesc> sprintPlanArray;
//	
//	public PerformanceIndexDataMaker(List<ISprintPlanDesc> sprintPlanArray , List<SprintBacklogMapper> sprintBacklogList ){
//		this.sprintPlanArray = sprintPlanArray; 
//		this.sprintBacklogList = sprintBacklogList;
//	}
	List<SprintBacklogLogic> sprintBacklogList;
	List<ISprintPlanDesc> sprintPlanArray;
	
	public PerformanceIndexDataMaker(List<ISprintPlanDesc> sprintPlanArray , List<SprintBacklogLogic> sprintBacklogList ){
		this.sprintPlanArray = sprintPlanArray; 
		this.sprintBacklogList = sprintBacklogList;
	}
	
	// earned value
	public List<Map.Entry<Integer,Double>> getEarnedValueTupleList( double baselineCostPerStoryPoint ){
		List<Map.Entry<Integer,Double>> earnedValueTuplelist = new ArrayList<Map.Entry<Integer,Double>>();	
		double earnedValue = 0;
		for( SprintBacklogLogic sprintBacklogLogic : sprintBacklogList ){  //sprintBacklog不能包含目前的正在執行的sprint
//			int sprintID = sprintBacklog.getSprintPlanId();
			long sprintId = sprintBacklogLogic.getSprintBacklogMapper().getSprintId();
			List<IIssue> storyArray = sprintBacklogLogic.getStories();
			for( IIssue story:storyArray ){
				if(story.getStatus().equals("closed")){  //completed 的 story
					earnedValue += Double.parseDouble(story.getEstimated())*baselineCostPerStoryPoint;  //才可以將該closed story points加入 
				}
			}
			earnedValueTuplelist.add( this.tuple(sprintId, earnedValue) );
		}
		return earnedValueTuplelist;
	}
	// plan value
	public List<Map.Entry<Integer,Double>> getPlanValueTupleList( double baselineCostPerSprintPoint, double baselineVelocity ){
		List<Map.Entry<Integer,Double>> planValueTuplelist = new ArrayList<Map.Entry<Integer,Double>>();		
		for( int i = 1 ; i <= sprintPlanArray.size() ; i ++ ){
			int sprintID = Integer.parseInt( sprintPlanArray.get(i-1).getID() );
			double planValue = i * baselineVelocity * baselineCostPerSprintPoint;
			planValueTuplelist.add( this.tuple(sprintID, planValue) );
		}
		return planValueTuplelist;
	}
	// total actual cost:單位，money
	public List<Map.Entry<Integer,Double>> getTotalActualCostTupleList(){
		List<Map.Entry<Integer,Double>> actualCostTuplelist = new ArrayList<Map.Entry<Integer,Double>>();
		double currentTotalActualCost = 0;
		for( ISprintPlanDesc sprintPlan : sprintPlanArray ){
			int sprintID = Integer.parseInt( sprintPlan.getID() );
			currentTotalActualCost += Double.parseDouble( sprintPlan.getActualCost() );
			actualCostTuplelist.add( this.tuple( sprintID, currentTotalActualCost));
		}
		return actualCostTuplelist;
	}
	
	// 沒有累積 的actual cost:單位，money
	public List<Map.Entry<Integer,Double>> getActualCostTupleList(){
		List<Map.Entry<Integer,Double>> actualCostTuplelist = new ArrayList<Map.Entry<Integer,Double>>();
		double actualCost = 0;
		for( ISprintPlanDesc sprintPlan : sprintPlanArray ){
			int sprintID = Integer.parseInt( sprintPlan.getID() );
			actualCost = Double.parseDouble( sprintPlan.getActualCost() );
			actualCostTuplelist.add( this.tuple( sprintID, actualCost));
		}
		return actualCostTuplelist;
	}
	
	public 	Double getActualCostBySprintID( int sprintID ){
		for( Map.Entry<Integer,Double> item : this.getActualCostTupleList()){
			if( item.getKey() == sprintID ){
				return item.getValue();
			}
		}
		return null;
	}
	
	/** calculate CPI(Cost Performance Index) **/
	public List<Map.Entry<Integer,Double>> getCostPerformanceIndexTupleList(double baselineCostPerStoryPoint){
		List<Map.Entry<Integer,Double>> costPerformanceIndexTupleList = new ArrayList<Map.Entry<Integer,Double>>();
		List<Map.Entry<Integer,Double>> earnedValueTupleList = this.getEarnedValueTupleList(baselineCostPerStoryPoint);
		List<Map.Entry<Integer,Double>> totalActualCostTupleList = this.getTotalActualCostTupleList();
		double tempCPIValue = 0;
		for(int i = 0; i < sprintPlanArray.size(); i++){
			if(totalActualCostTupleList.get( i ).getValue() != 0){  // 防止有NaN和INFINITY的數出現
				tempCPIValue = earnedValueTupleList.get( i ).getValue()/ totalActualCostTupleList.get( i ).getValue();
			}else{
				tempCPIValue = 0;
			}
			int sprintID = Integer.parseInt( sprintPlanArray.get(i).getID() );
			costPerformanceIndexTupleList.add( this.tuple( sprintID, calculateNumberToRounding(tempCPIValue) ) );  // 將CPI四捨五入至小數點後第三位
		}
		return costPerformanceIndexTupleList;
	}
	/** calculate SPI(Schedule Performance Index) **/
	public List<Map.Entry<Integer,Double>> getSchedulePerformanceIndexTupleList( double baselineCostPerSprintPoint, double baselineVelocity ){
		List<Map.Entry<Integer,Double>> schedulePerformanceIndexTupleList = new ArrayList<Map.Entry<Integer,Double>>();
		List<Map.Entry<Integer,Double>> earnedValueTupleList = this.getEarnedValueTupleList( baselineCostPerSprintPoint );
		List<Map.Entry<Integer,Double>> planValueTupleList   = this.getPlanValueTupleList( baselineCostPerSprintPoint, baselineVelocity );
		double tempSPIValue = 0;
		for( int i = 0 ; i < sprintPlanArray.size() ; i ++ ){
			if( planValueTupleList.get( i ).getValue() != 0 ){	 // 防止有NaN和INFINITY的數出現
				tempSPIValue = earnedValueTupleList.get( i ).getValue()/ planValueTupleList.get( i ).getValue();
			}else{
				tempSPIValue = 0;
			}
			int sprintID = Integer.parseInt( sprintPlanArray.get(i).getID() );
			schedulePerformanceIndexTupleList.add( this.tuple( sprintID, calculateNumberToRounding(tempSPIValue) ) );
		}
		return schedulePerformanceIndexTupleList;
	}
	// 四捨五入至第三位
	private Double calculateNumberToRounding(double oriNumber){
		double afterRoundingNumberValue = 0;
		BigDecimal big = new BigDecimal(oriNumber);
		BigDecimal bigNumber = big.setScale(3,BigDecimal.ROUND_HALF_UP);
		afterRoundingNumberValue = bigNumber.doubleValue();
		return afterRoundingNumberValue;
	}

	public Map.Entry<Integer, Double> tuple(Integer key, Double value) {
	    Map<Integer, Double> map = new HashMap<Integer, Double>();
	    map.put(key, value);
	    return map.entrySet().iterator().next();
	}

}
