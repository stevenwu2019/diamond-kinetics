import java.util.*;
import java.io.*;
import java.lang.*;
class SwingPoints //data structure to represent all swing data points
{ 
    private int time; //time data point
	private float[] data; //holds remaining 6 motion data points
	
	SwingPoints() //constructor
	{
		time = 0;
		data = new float[6];
	}
	void setTime(int inTime) //set time stamp
	{
		time = inTime;
	}
	void setData(int index, float inDat) //set data points in array
	{
		data[index] = inDat;
	}
	int getTime() //returns time stamp
	{
		return time;
	}
	float getData(int index) //returns specified data point from data array
	{
		return data[index];
	}
}
class IndexPair //stores pairs of indices for output of searchMultiContinuityWithinRange
{
	private int startIndex;
	private int endIndex;

	IndexPair() //constructor
	{
		startIndex = 0;
		endIndex = 0;
	}
	IndexPair(int start, int end) //alternate constructor
	{
		startIndex = start;
		endIndex = end;
	}
	void setStartIndex(int start) //set startIndex
	{
		startIndex = start;
	}
	void setEndIndex(int end) //set endIndex
	{
		endIndex = end;
	}
	int getStartIndex() //returns startIndex
	{
		return startIndex;
	}
	int getEndIndex() //returns endIndex
	{
		return endIndex;
	}
}
public class SwingData
{
	//index data columns
	enum DataType 
	{
		ax, ay, az, wx, wy, wz;
	}

	//scan in data from input file
	static Scanner input = null;
	
	public static void main(String[] args)
	{
		//search for data file
		try {
			input = new Scanner(new FileInputStream("./latestSwing.csv"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		input.useDelimiter(",|\r\n"); //parse by commas or newlines
		
		ArrayList<SwingPoints> swingPtsList = new ArrayList<SwingPoints>(); //stores swing data points
		
		while(input.hasNext()) //reads in all data
		{
			SwingPoints swingPt = new SwingPoints(); //struct to contain each time instant of a swing data point
			int inDat = input.nextInt(); //timestamp
			swingPt.setTime(inDat);
			for(int i = 0; i < 6; i++) //motion data
			{
				float inFlo = input.nextFloat();
				swingPt.setData(i, inFlo);
			}
			swingPtsList.add(swingPt); //add to storage list
		}
	}
	
/*  From indexBegin to indexEnd, search data for values that are higher than threshold. 
	Return the first index where data has values that meet this criteria for at least winLength samples in a row.  */
	int searchContinuityAboveValue(DataType data, int indexBegin, int indexEnd, float threshold, int winLength)
	{
		int i = indexBegin;
		while(i + winLength - 1 <= indexEnd && i + winLength <= swingPtsList.size()) //check the data we want to access exists and is within range
		{
			if(swingPtsList.get(i).getData(data.ordinal()) > threshold) //if threshold met
			{
				int j = 0; //save value of j after loop
				for(j = i+1; j < i + winLength; j++) //check the next samples within winLength
				{
					if(swingPtsList.get(j).getData(data.ordinal()) <= threshold) break; //if not at least winLength in a row, move on
					//saves time; we skip data points we know are not satisfactory, only check each point once
				}
				if(j - i == winLength) return i; //identified first index that satisfies conditions
				else i = j + 1; //continue searching
			}
			else
			{
				i++; //continue searching
			}
		}
		return -1; //no satisfactory index found
	}

/*  From indexBegin to indexEnd (where indexBegin is larger than indexEnd), search data for values that are higher than thresholdLo and lower than thresholdHi. 
	Return the first index where data has values that meet this criteria for at least winLength samples in a row.  */
	int backSearchContinuityWithinRange(DataType data, int indexBegin, int indexEnd, float thresholdLo, float thresholdHi, int winLength)
	{
		int i = indexBegin;
		while(i - winLength + 1 >= indexEnd && i - winLength + 1 >= 0) //check the data we want to access exists and is within range
		{
			if(swingPtsList.get(i).getData(data.ordinal()) > thresholdLo && swingPtsList.get(i).getData(data.ordinal()) < thresholdHi) //if thresholds met
			{
				int j = 0; //save value of j after loop
				for(j = i-1; j > i - winLength; j--) //check the next samples within winLength
				{
					if(swingPtsList.get(j).getData(data.ordinal()) <= thresholdLo || swingPtsList.get(j).getData(data.ordinal()) >= thresholdHi) break; //if not at least winLength in a row, move on
					//saves time; we skip data points we know are not satisfactory, only check each point once
				}
				if(i - j == winLength) return i; //identified first index that satisfies conditions
				else i = j - 1; //continue searching
			}
			else
			{
				i--; //continue searching
			}
		}
		return -1; //no satisfactory index found
	}

/*	From indexBegin to indexEnd, search data1 for values that are higher than threshold1 and also search data2 for values that are higher than threshold2. 
	Return the first index where both data1 and data2 have values that meet these criteria for at least winLength samples in a row.  */
	int searchContinuityAboveValueTwoSignals(DataType data1, DataType data2, int indexBegin, int indexEnd, float threshold1, float threshold2, int winLength)
	{
		int i = indexBegin;
		while(i + winLength - 1 <= indexEnd && i + winLength <= swingPtsList.size()) //check the data we want to access exists and is within range
		{
			if(swingPtsList.get(i).getData(data1.ordinal()) > threshold1 && swingPtsList.get(i).getData(data2.ordinal()) > threshold2) //if thresholds met
			{
				int j = 0; //save value of j after loop
				for(j = i+1; j < i + winLength; j++) //check the next samples within winLength
				{
					if(swingPtsList.get(j).getData(data1.ordinal()) <= threshold1 || swingPtsList.get(j).getData(data2.ordinal()) <= threshold2) break; //if not at least winLength in a row, move on
					//saves time; we skip data points we know are not satisfactory, only check each point once
				}
				if(j - i == winLength) return i; //identified first index that satisfies conditions
				else i = j + 1; //continue searching
			}
			else
			{
				i++; //continue searching
			}
		}
		return -1; //no satisfactory index found
	}
	
/*	From indexBegin to indexEnd, search data for values that are higher than thresholdLo and lower than thresholdHi. 
	Return the starting index and ending index of all continuous samples that meet this criteria for at least winLength data points.  */
	ArrayList<IndexPair> searchMultiContinuityWithinRange(DataType data, int indexBegin, int indexEnd, float thresholdLo, float thresholdHi, int winLength)
	{
		ArrayList<IndexPair> results = new ArrayList<IndexPair>(); //list of all satisfactory ranges
		int i = indexBegin;
		while(i + winLength - 1 <= indexEnd && i + winLength <= swingPtsList.size()) //check the data we want to access exists and is within range
		{
			if(swingPtsList.get(i).getData(data.ordinal()) > thresholdLo && swingPtsList.get(i).getData(data.ordinal()) > thresholdHi) //if thresholds met
			{
				int j = 0; //save value of j after loop
				for(j = i+1; j < i + winLength; j++) //check the next samples within winLength
				{
					if(swingPtsList.get(j).getData(data.ordinal()) <= thresholdLo || swingPtsList.get(j).getData(data.ordinal()) >= thresholdHi) break; //if not at least winLength in a row, move on
					//saves time; we skip data points we know are not satisfactory, only check each point once
				}
				if(j - i == winLength) //identified starting index that satisfies conditions
				{
					int startIndex = i; 
					int endIndex = 0;
					//look to see if there are more consecutive satisfactory data points
					while(j <= indexEnd && j < swingPtsList.size()
					&& swingPtsList.get(j).getData(data.ordinal()) > thresholdLo && swingPtsList.get(j).getData(data.ordinal()) > thresholdHi)
					{
						j++;
					}
					endIndex = j - 1; //close the range of satisfactory data points
					IndexPair addPair = new IndexPair(startIndex, endIndex); //create index pair
					results.add(addPair); //add index pair to list of outputs
					i = j; //continue searching
				} 
				else i = j + 1; //continue searching
			}
			else
			{
				i++; //continue searching
			}
		}
		return results;
	}
}

//answer to bonus question: impact occurs in latestSwing.csv around the 873rd row (index 872), which has the timestamp 1088848