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
  userId: number;
  username: string;
  email: string;
  height?: number;
  currentWeight?: number;
  age?: number;
  sex?: string;
  activityLevel?: string;
  goal?: string;
  dailyCalorieGoal?: number;
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



