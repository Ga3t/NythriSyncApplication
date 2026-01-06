export interface LoginRequest {
  login: string;
  password: string;
}
export interface RegistrationRequest {
  username: string;
  password: string;
  email: string;
}
export interface AuthResponse {
  jwtToken: string;
  refreshToken: string;
}