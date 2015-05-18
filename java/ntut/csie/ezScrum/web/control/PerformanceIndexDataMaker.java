/**
 * Performance Index includes SPI and CPI
 * **/
package ntut.csie.ezScrum.web.control;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;

public class PerformanceIndexDataMaker {
	
//	List<SprintBacklogMapper> sprintBacklogList;
//	List<ISprintPlanDesc> sprintPlanArray;
//	
//	public PerformanceIndexDataMaker(List<ISprintPlanDesc> sprintPlanArray , List<SprintBacklogMapper> sprintBacklogList ){
//		this.sprintPlanArray = sprintPlanArray; 
//		this.sprintBacklogList = sprintBacklogList;
//	}
	List<SprintBacklogLogic> mSprintBacklogList;
	List<ISprintPlanDesc> mSprintPlanArray;
	
	public PerformanceIndexDataMaker(List<ISprintPlanDesc> sprintPlanArray , List<SprintBacklogLogic> sprintBacklogList ){
		this.mSprintPlanArray = sprintPlanArray; 
		this.mSprintBacklogList = sprintBacklogList;
	}
	
	// earned value
	public List<Entry<Long, Double>> getEarnedValueTupleList(double baselineCostPerStoryPoint) {
		List<Map.Entry<Long, Double>> earnedValueTuplelist = new ArrayList<Map.Entry<Long, Double>>();
		double earnedValue = 0;
		for (SprintBacklogLogic sprintBacklogLogic : mSprintBacklogList) {  //sprintBacklog不能包含目前的正在執行的sprint
			// int sprintID = sprintBacklog.getSprintPlanId();
			long sprintId = sprintBacklogLogic.getSprintBacklogMapper().getSprintId();
			ArrayList<StoryObject> storyArray = sprintBacklogLogic.getStories();
			for (StoryObject story : storyArray) {
				// completed story
				if (story.getStatus() == StoryObject.STATUS_DONE) {
					earnedValue += story.getEstimate() * baselineCostPerStoryPoint;  //才可以將該closed story points加入 
				}
			}
			earnedValueTuplelist.add(tuple(sprintId, earnedValue));
		}
		return earnedValueTuplelist;
	}

	// plan value
	public List<Entry<Long, Double>> getPlanValueTupleList(double baselineCostPerSprintPoint, double baselineVelocity) {
		List<Map.Entry<Long, Double>> planValueTuplelist = new ArrayList<Map.Entry<Long, Double>>();
		for (int i = 1; i <= mSprintPlanArray.size(); i++) {
			long sprintID = Long.parseLong((mSprintPlanArray.get(i - 1).getID()));
			double planValue = i * baselineVelocity * baselineCostPerSprintPoint;
			planValueTuplelist.add(tuple(sprintID, planValue));
		}
		return planValueTuplelist;
	}

	// total actual cost:單位，money
	public List<Map.Entry<Long, Double>> getTotalActualCostTupleList() {
		List<Map.Entry<Long, Double>> actualCostTuplelist = new ArrayList<Map.Entry<Long, Double>>();
		double currentTotalActualCost = 0;
		for (ISprintPlanDesc sprintPlan : mSprintPlanArray) {
			long sprintID = Long.parseLong((sprintPlan.getID()));
			currentTotalActualCost += Double.parseDouble(sprintPlan.getActualCost());
			actualCostTuplelist.add(tuple(sprintID, currentTotalActualCost));
		}
		return actualCostTuplelist;
	}
	
	// 沒有累積 的actual cost:單位，money
	public List<Map.Entry<Long, Double>> getActualCostTupleList() {
		List<Map.Entry<Long, Double>> actualCostTuplelist = new ArrayList<Map.Entry<Long, Double>>();
		double actualCost = 0;
		for (ISprintPlanDesc sprintPlan : mSprintPlanArray) {
			long sprintID = Long.parseLong((sprintPlan.getID()));
			actualCost = Double.parseDouble(sprintPlan.getActualCost());
			actualCostTuplelist.add(tuple(sprintID, actualCost));
		}
		return actualCostTuplelist;
	}
	
	public Double getActualCostBySprintID(long sprintID) {
		for (Entry<Long, Double> item : getActualCostTupleList()) {
			if (item.getKey() == sprintID) {
				return item.getValue();
			}
		}
		return null;
	}
	
	/** calculate CPI(Cost Performance Index) **/
	public List<Map.Entry<Long, Double>> getCostPerformanceIndexTupleList(double baselineCostPerStoryPoint) {
		List<Map.Entry<Long, Double>> costPerformanceIndexTupleList = new ArrayList<Map.Entry<Long, Double>>();
		List<Map.Entry<Long, Double>> earnedValueTupleList = getEarnedValueTupleList(baselineCostPerStoryPoint);
		List<Map.Entry<Long, Double>> totalActualCostTupleList = getTotalActualCostTupleList();
		double tempCPIValue = 0;
		for (int i = 0; i < mSprintPlanArray.size(); i++) {
			if (totalActualCostTupleList.get(i).getValue() != 0) {  // 防止有NaN和INFINITY的數出現
				tempCPIValue = earnedValueTupleList.get(i).getValue() / totalActualCostTupleList.get(i).getValue();
			} else {
				tempCPIValue = 0;
			}
			int sprintID = Integer.parseInt(mSprintPlanArray.get(i).getID());
			costPerformanceIndexTupleList.add(this.tuple(sprintID, calculateNumberToRounding(tempCPIValue)));  // 將CPI四捨五入至小數點後第三位
		}
		return costPerformanceIndexTupleList;
	}

	/** calculate SPI(Schedule Performance Index) **/
	public List<Map.Entry<Long, Double>> getSchedulePerformanceIndexTupleList(double baselineCostPerSprintPoint, double baselineVelocity) {
		List<Map.Entry<Long, Double>> schedulePerformanceIndexTupleList = new ArrayList<Map.Entry<Long, Double>>();
		List<Map.Entry<Long, Double>> earnedValueTupleList = this.getEarnedValueTupleList(baselineCostPerSprintPoint);
		List<Map.Entry<Long, Double>> planValueTupleList = this.getPlanValueTupleList(baselineCostPerSprintPoint, baselineVelocity);
		double tempSPIValue = 0;
		for (int i = 0; i < mSprintPlanArray.size(); i++) {
			if (planValueTupleList.get(i).getValue() != 0) {	 // 防止有NaN和INFINITY的數出現
				tempSPIValue = earnedValueTupleList.get(i).getValue() / planValueTupleList.get(i).getValue();
			} else {
				tempSPIValue = 0;
			}
			long sprintID = Long.parseLong(mSprintPlanArray.get(i).getID());
			schedulePerformanceIndexTupleList.add(tuple(sprintID, calculateNumberToRounding(tempSPIValue)));
		}
		return schedulePerformanceIndexTupleList;
	}

	// 四捨五入至第三位
	private Double calculateNumberToRounding(double oriNumber) {
		double afterRoundingNumberValue = 0;
		BigDecimal big = new BigDecimal(oriNumber);
		BigDecimal bigNumber = big.setScale(3, BigDecimal.ROUND_HALF_UP);
		afterRoundingNumberValue = bigNumber.doubleValue();
		return afterRoundingNumberValue;
	}

	public Map.Entry<Long, Double> tuple(long sprintId, Double value) {
		Map<Long, Double> map = new HashMap<Long, Double>();
		map.put(sprintId, value);
		return map.entrySet().iterator().next();
	}

}
