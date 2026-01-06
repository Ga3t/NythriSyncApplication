export interface UserDetailsDto {
  currentWeight: number;
  birthDay: string;
  sex: string;
  activityType: number;
  goalType: string;
  wantedWeight: number;
  height: number;
}
export interface UpdateUserDetailsDto {
  height?: number;
  weight?: number;
  age?: number;
  sex?: string;
  activityLevel?: string;
  goal?: string;
}
export interface UserInfoResponse {
  weight?: number;
  height?: number;
  sex?: string;
  activity_type?: string;
  birthday_date?: string;
  goalType?: string;
}
export interface UserDetailsResponse {
  height: number;
  weight: number;
  age: number;
  sex: string;
  activityLevel: string;
  goal: string;
  dailyCalorieGoal: number;
}