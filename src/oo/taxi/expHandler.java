package oo.taxi;

import java.util.regex.Pattern;

public class expHandler 
/**
 * @OVERVIEW :
 * expHandler will identify whether input string has correct format
 * if true, return all elements within int[][]
 * if false, return null
 */
{
	
	public boolean repOK(){
		return true;
	}
	/**
	 * @REQIRES : None;
	 * @MODIFIES : int[][] nums;
	 * @EFFECTS : 
	 * (\all input not matches REGEX) ==> \result = null;
	 * (\all input matches REGEX) ==> \result = nums;
	 */
	public int[][] checkInput(String input){
		input = input.replaceAll(" +", "");
		String REGEX_CR = "\\[CR,\\((\\d|[1-7][0-9]),(\\d|[1-7][0-9])\\),\\((\\d|[1-7][0-9]),(\\d|[1-7][0-9])\\)\\]";
		String REGEX_ST = "\\[ST,\\((\\d|[1-7][0-9]),(\\d|[1-7][0-9])\\),\\((\\d|[1-7][0-9]),(\\d|[1-7][0-9])\\),(0|1)\\]";
		Pattern pattern_cr = Pattern.compile(REGEX_CR);
		Pattern pattern_st = Pattern.compile(REGEX_ST);
		boolean match_cr = pattern_cr.matcher(input).matches();
		boolean match_st = pattern_st.matcher(input).matches();
		boolean match = match_cr | match_st;
		if (match == false) return null;
		else{
			if (match_cr == true){
				int[][] nums = new int[2][2];
				input = input.replaceAll("[^\\d,]", ""); input = input.substring(1, input.length());         //,x1,y1,x2,y2
				String[] remains = input.split(",");
				for (int i = 0 ; i < remains.length ; i++){
					switch(i){
					case 0 : nums[0][0] = Integer.parseInt(remains[i]); break;
					case 1 : nums[0][1] = Integer.parseInt(remains[i]); break;
					case 2 : nums[1][0] = Integer.parseInt(remains[i]); break;
					case 3 : nums[1][1] = Integer.parseInt(remains[i]); break;
					}
				}
				if (nums[0][0] == nums[1][0] && nums[0][1] == nums[1][1]) return null;
				return nums;
			}
			else if (match_st == true){
				int[][] nums = new int[3][2];
				input = input.replaceAll("[^\\d,]", ""); input = input.substring(1, input.length()); //x1,y1,x2,y2,status
				String[] remains = input.split(",");
				for (int i = 0 ; i < 4 ; i++){
					switch(i){
					case 0 : nums[0][0] = Integer.parseInt(remains[i]); break;
					case 1 : nums[0][1] = Integer.parseInt(remains[i]); break;
					case 2 : nums[1][0] = Integer.parseInt(remains[i]); break;
					case 3 : nums[1][1] = Integer.parseInt(remains[i]); break;
					}
				}
				nums[2][0] = Integer.parseInt(remains[4]);
				return nums;
			}
			else return null;
		}
	}
}
