import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MainPageResponse, CalendarResponse, MealByDateResponse } from '../models/calorie.models';
import { MealDto, MealType } from '../models/food.models';
import { UserDetailsDto, UpdateUserDetailsDto, UserInfoResponse, UserDetailsResponse } from '../models/user.models';
import { ReportResponse } from '../models/analytics.models';
@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) {}
  saveMeal(mealType: MealType, date: string, meal: MealDto): Observable<string> {
    const params = new HttpParams()
      .set('MealType', mealType)
      .set('DateTime', date);
    return this.http.post<string>(`${this.apiUrl}/calapp/savemeal`, meal, { params, responseType: 'text' as 'json' });
  }
  getMealByDate(date: string, mealType: MealType): Observable<MealByDateResponse> {
    const params = new HttpParams()
      .set('date', date)
      .set('MealType', mealType);
    return this.http.get<MealByDateResponse>(`${this.apiUrl}/calapp/showmeal`, { params });
  }
  getMainPageInfo(): Observable<MainPageResponse> {
    return this.http.get<MainPageResponse>(`${this.apiUrl}/calapp/mainpage`);
  }
  getMainPageInfoNew(): Observable<MainPageResponse> {
    return this.http.get<MainPageResponse>(`${this.apiUrl}/calapp/mainpage`);
  }
  getPageByDate(date: string): Observable<MainPageResponse> {
    const params = new HttpParams().set('Date', date);
    return this.http.get<MainPageResponse>(`${this.apiUrl}/calapp/pageByDate`, { params });
  }
  addWater(date: string, waterAmount: number): Observable<number> {
    const params = new HttpParams()
      .set('Date', date)
      .set('Water-To-Add', waterAmount.toString());
    return this.http.post<number>(`${this.apiUrl}/calapp/addwater`, null, { params });
  }
  getCalendar(year: number): Observable<CalendarResponse> {
    const params = new HttpParams().set('year', year.toString());
    return this.http.get<CalendarResponse>(`${this.apiUrl}/calapp/calendar`, { params });
  }
  setUserDetails(userDetails: UserDetailsDto): Observable<UserDetailsResponse> {
    return this.http.post<UserDetailsResponse>(`${this.apiUrl}/userdetails/setuserdetails`, userDetails);
  }
  updateUserDetails(userDetails: UpdateUserDetailsDto): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/userdetails/updateuserdetails`, userDetails, {
      responseType: 'text' as 'json'
    });
  }
  userDetailsExists(): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/userdetails/userdetailsexists`, {
      withCredentials: true
    });
  }
  getUserInfo(): Observable<UserInfoResponse> {
    return this.http.get<UserInfoResponse>(`${this.apiUrl}/userdetails/info`);
  }
  addWeight(weight: number): Observable<number> {
    const params = new HttpParams().set('new_weight', weight.toString());
    return this.http.get<number>(`${this.apiUrl}/userdetails/newWeighing`, { params });
  }
  getReport(startDate: string, endDate: string): Observable<ReportResponse> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<ReportResponse>(`${this.apiUrl}/analyse/reports`, { params });
  }
  getWeekReport(): Observable<ReportResponse> {
    return this.http.get<ReportResponse>(`${this.apiUrl}/analyse/reportforweek`);
  }
  searchProduct(barcode: string): Observable<any> {
    const params = new HttpParams().set('barcode', barcode);
    return this.http.get(`${this.apiUrl}/product/search`, { params });
  }
  searchFood(query: string, page: number = 0): Observable<any> {
    const params = new HttpParams()
      .set('search_expression', query)
      .set('page', page.toString());
    return this.http.get(`${this.apiUrl}/foodsecret/search`, { params });
  }
  getFoodDetails(foodId: string): Observable<any> {
    const params = new HttpParams().set('id', foodId);
    return this.http.get(`${this.apiUrl}/foodsecret/details`, { params });
  }
  searchProductByBarcode(barcode: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/product/findbybarcode/${barcode}`);
  }
}