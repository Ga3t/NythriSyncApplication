import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { ReportResponse, DayAnalyse } from '../../../../core/models/analytics.models';
import { MatSnackBar } from '@angular/material/snack-bar';

type FilterPeriod = '7days' | 'month' | 'halfyear' | 'year';

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss']
})
export class AnalyticsComponent implements OnInit {
  reportData: ReportResponse | null = null;
  loading = false;
  selectedFilter: FilterPeriod = '7days';
  tooltip: { visible: boolean; x: number; y: number; date: string; consumed: number; norm: number; metric: string; unit: string } | null = null;

  constructor(
    private apiService: ApiService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadReport('7days');
  }

  loadReport(period: FilterPeriod): void {
    this.loading = true;
    this.selectedFilter = period;
    
    const { startDate, endDate } = this.getDateRangeForPeriod(period);
    const startDateStr = startDate.toISOString().split('T')[0];
    const endDateStr = endDate.toISOString().split('T')[0];

    this.apiService.getReport(startDateStr, endDateStr).subscribe({
      next: (data) => {
        this.reportData = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.snackBar.open('Failed to load report', 'Close', { duration: 3000 });
      }
    });
  }

  getDateRangeForPeriod(period: FilterPeriod): { startDate: Date; endDate: Date } {
    const endDate = new Date();
    endDate.setHours(23, 59, 59, 999);
    const startDate = new Date();

    switch (period) {
      case '7days':
        startDate.setDate(endDate.getDate() - 6);
        startDate.setHours(0, 0, 0, 0);
        break;
      case 'month':
        startDate.setDate(endDate.getDate() - 29); 
        startDate.setHours(0, 0, 0, 0);
        break;
      case 'halfyear':
        startDate.setDate(endDate.getDate() - 179); 
        startDate.setHours(0, 0, 0, 0);
        break;
      case 'year':
        startDate.setDate(endDate.getDate() - 364); 
        startDate.setHours(0, 0, 0, 0);
        break;
    }

    return { startDate, endDate };
  }

  chartTypes = [
    { key: 'calories', label: 'Calories', consumedKey: 'kcalCons', normKey: 'kcalNorm', unit: 'kcal', consumedColor: '#66bb6a', normColor: '#2e7d32', areaColor: 'rgba(102, 187, 106, 0.4)' },
    { key: 'sugar', label: 'Sugar', consumedKey: 'sugarCons', normKey: 'sugarNorm', unit: 'g', consumedColor: '#800020', normColor: '#5c0017', areaColor: 'rgba(128, 0, 32, 0.4)' },
    { key: 'fiber', label: 'Fiber', consumedKey: 'fiberCons', normKey: 'fiberNorm', unit: 'g', consumedColor: '#e3dac9', normColor: '#c4b5a0', areaColor: 'rgba(227, 218, 201, 0.4)', checkered: true },
    { key: 'fats', label: 'Fats', consumedKey: 'fatCons', normKey: 'fatNorm', unit: 'g', consumedColor: '#FF9800', normColor: '#E65100', areaColor: 'rgba(255, 152, 0, 0.4)' },
    { key: 'protein', label: 'Protein', consumedKey: 'proteinCons', normKey: 'proteinNorm', unit: 'g', consumedColor: '#E91E63', normColor: '#C2185B', areaColor: 'rgba(233, 30, 99, 0.4)' },
    { key: 'carbs', label: 'Carbohydrates', consumedKey: 'carbsCons', normKey: 'carbsNorm', unit: 'g', consumedColor: '#9C27B0', normColor: '#6A1B9A', areaColor: 'rgba(156, 39, 176, 0.4)' },
    { key: 'water', label: 'Water', consumedKey: 'waterCons', normKey: 'waterNorm', unit: 'ml', consumedColor: '#2196F3', normColor: '#1565C0', areaColor: 'rgba(33, 150, 243, 0.4)' },
    { key: 'weight', label: 'Weight', consumedKey: 'weight', normKey: null, unit: 'kg', consumedColor: '#8B4513', normColor: '#654321', areaColor: 'rgba(139, 69, 19, 0.4)', singleLine: true }
  ];

  getLineChartWidth(): number {
    return 800;
  }

  getLineChartHeight(): number {
    return 250;
  }

  getLineChartPadding(): { top: number; right: number; bottom: number; left: number } {
    return { top: 20, right: 40, bottom: 45, left: 60 };
  }

  getChartData(metricKey: string): any[] {
    if (!this.reportData?.anlyses || this.reportData.anlyses.length === 0) return [];
    
    const metric = this.chartTypes.find(m => m.key === metricKey);
    if (!metric) return [];
    
    return this.reportData.anlyses.map(day => {
      const consumed = (day as any)[metric.consumedKey] || 0;
      const normal = metric.normKey ? ((day as any)[metric.normKey] || 0) : 0;
      return {
        date: day.date,
        consumed: consumed,
        normal: normal,
        diff: consumed - normal,
        hasData: consumed > 0 || normal > 0
      };
    }).filter(day => day.hasData || metricKey === 'weight'); 
  }

  getMaxChartValue(metricKey: string): number {
    const data = this.getChartData(metricKey);
    if (data.length === 0) return 100;
    const maxConsumed = Math.max(...data.map(d => d.consumed), 0);
    const maxNormal = Math.max(...data.map(d => d.normal), 0);
    const maxValue = Math.max(maxConsumed, maxNormal, 1);
    return maxValue * 1.1;
  }

  getYAxisLabels(metricKey: string): { value: number; y: number }[] {
    const maxValue = this.getMaxChartValue(metricKey);
    const padding = this.getLineChartPadding();
    const height = this.getLineChartHeight();
    const chartHeight = height - padding.top - padding.bottom;
    
    const labels: { value: number; y: number }[] = [];
    const numLabels = 4;
    
    for (let i = 0; i <= numLabels; i++) {
      const value = (maxValue / numLabels) * (numLabels - i);
      const y = padding.top + (i / numLabels) * chartHeight;
      labels.push({ value, y });
    }
    
    return labels;
  }

  getChartDataPoints(metricKey: string): { consumed: { x: number; y: number }[]; norm: { x: number; y: number }[] } {
    const data = this.getChartData(metricKey);
    if (data.length === 0) return { consumed: [], norm: [] };
    
    const maxValue = this.getMaxChartValue(metricKey);
    const padding = this.getLineChartPadding();
    const width = this.getLineChartWidth();
    const height = this.getLineChartHeight();
    const chartWidth = width - padding.left - padding.right;
    const chartHeight = height - padding.top - padding.bottom;

    const consumedPoints: { x: number; y: number }[] = [];
    const normPoints: { x: number; y: number }[] = [];

    data.forEach((day, index) => {
      const x = padding.left + (index / (data.length - 1 || 1)) * chartWidth;
      const consumedY = padding.top + chartHeight - (day.consumed / maxValue) * chartHeight;
      const normY = padding.top + chartHeight - (day.normal / maxValue) * chartHeight;
      
      consumedPoints.push({ x, y: consumedY });
      normPoints.push({ x, y: normY });
    });

    return { consumed: consumedPoints, norm: normPoints };
  }

  getLinePath(points: { x: number; y: number }[]): string {
    if (points.length === 0) return '';
    return points.map((point, index) => {
      return index === 0 ? `M ${point.x} ${point.y}` : `L ${point.x} ${point.y}`;
    }).join(' ');
  }

  getLastDataPoint(metricKey: string): { consumed: { x: number; y: number } | null; norm: { x: number; y: number } | null } {
    const { consumed: consumedPoints, norm: normPoints } = this.getChartDataPoints(metricKey);
    const lastConsumed = consumedPoints.length > 0 ? consumedPoints[consumedPoints.length - 1] : null;
    const lastNorm = normPoints.length > 0 ? normPoints[normPoints.length - 1] : null;
    return { consumed: lastConsumed, norm: lastNorm };
  }

  getAreaPaths(metricKey: string): { deficit: string; surplus: string } {
    const metric = this.chartTypes.find(m => m.key === metricKey);
    if (metric?.singleLine) return { deficit: '', surplus: '' };
    
    const data = this.getChartData(metricKey);
    if (data.length === 0) return { deficit: '', surplus: '' };
    
    const { consumed: consumedPoints, norm: normPoints } = this.getChartDataPoints(metricKey);
    const deficitSegments: string[] = [];
    const surplusSegments: string[] = [];

    for (let i = 0; i < consumedPoints.length - 1; i++) {
      const c1 = consumedPoints[i];
      const c2 = consumedPoints[i + 1];
      const n1 = normPoints[i];
      const n2 = normPoints[i + 1];

      const isDeficit1 = c1.y > n1.y;
      const isDeficit2 = c2.y > n2.y;

      if (isDeficit1 && isDeficit2) {
        deficitSegments.push(`M ${c1.x} ${c1.y} L ${c2.x} ${c2.y} L ${n2.x} ${n2.y} L ${n1.x} ${n1.y} Z`);
      } else if (!isDeficit1 && !isDeficit2) {
        surplusSegments.push(`M ${n1.x} ${n1.y} L ${n2.x} ${n2.y} L ${c2.x} ${c2.y} L ${c1.x} ${c1.y} Z`);
      } else {
        const denom = (c2.y - c1.y) - (n2.y - n1.y);
        if (Math.abs(denom) > 0.001) {
          const t = (n1.y - c1.y) / denom;
          const ix = c1.x + t * (c2.x - c1.x);
          const iy = c1.y + t * (c2.y - c1.y);

          if (isDeficit1) {
            deficitSegments.push(`M ${c1.x} ${c1.y} L ${ix} ${iy} L ${n1.x} ${n1.y} Z`);
            surplusSegments.push(`M ${ix} ${iy} L ${c2.x} ${c2.y} L ${n2.x} ${n2.y} Z`);
          } else {
            surplusSegments.push(`M ${n1.x} ${n1.y} L ${ix} ${iy} L ${c1.x} ${c1.y} Z`);
            deficitSegments.push(`M ${ix} ${iy} L ${n2.x} ${n2.y} L ${c2.x} ${c2.y} Z`);
          }
        }
      }
    }

    return {
      deficit: deficitSegments.join(' '),
      surplus: surplusSegments.join(' ')
    };
  }

  getPatternId(metricKey: string): string {
    return `deficit-pattern-${metricKey}`;
  }

  getCheckeredPatternId(metricKey: string): string {
    return `checkered-pattern-${metricKey}`;
  }

  getMetricConfig(metricKey: string): any {
    return this.chartTypes.find(m => m.key === metricKey);
  }

  getDateLabel(dateStr: string): string {
    const date = new Date(dateStr);
    const dayNames = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    return dayNames[date.getDay()];
  }

  getDateShortLabel(dateStr: string): string {
    const date = new Date(dateStr);
    const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    return `${monthNames[date.getMonth()]} ${date.getDate()}`;
  }

  formatValue(value: number, unit: string): string {
    if (unit === 'kcal' || unit === 'ml') {
      return Math.round(value).toString();
    } else {
      return value.toFixed(1);
    }
  }


  showTooltip(event: MouseEvent, metricKey: string, dayIndex: number): void {
    const data = this.getChartData(metricKey);
    if (dayIndex < 0 || dayIndex >= data.length) return;
    
    const day = data[dayIndex];
    const metric = this.chartTypes.find(m => m.key === metricKey);
    if (!metric) return;

    const chartContainer = (event.currentTarget as HTMLElement).closest('.chart-container');
    if (!chartContainer) return;
    
    const containerRect = chartContainer.getBoundingClientRect();
    const points = this.getChartDataPoints(metricKey);
    const point = points.consumed[dayIndex];
    
    if (!point) return;

    const svg = (event.currentTarget as HTMLElement).closest('svg');
    if (!svg) return;
    
    const svgRect = svg.getBoundingClientRect();
    const scaleX = svgRect.width / this.getLineChartWidth();
    const scaleY = svgRect.height / this.getLineChartHeight();
    
    this.tooltip = {
      visible: true,
      x: point.x * scaleX,
      y: point.y * scaleY - 80,
      date: day.date,
      consumed: day.consumed,
      norm: day.normal,
      metric: metric.label,
      unit: metric.unit
    };
  }

  hideTooltip(): void {
    if (this.tooltip) {
      this.tooltip.visible = false;
    }
  }

  navigateToDate(date: string): void {
    const dateStr = date.split('T')[0]; 
    this.router.navigate(['/dashboard/date', dateStr]);
  }

  getDataPointAtX(metricKey: string, x: number): number {
    const data = this.getChartData(metricKey);
    if (data.length === 0) return -1;
    
    const padding = this.getLineChartPadding();
    const width = this.getLineChartWidth();
    const chartWidth = width - padding.left - padding.right;
    const relativeX = x - padding.left;
    
    if (relativeX < 0 || relativeX > chartWidth) return -1;
    
    const index = Math.round((relativeX / chartWidth) * (data.length - 1));
    return Math.max(0, Math.min(index, data.length - 1));
  }

  onChartHover(event: MouseEvent, metricKey: string): void {
    const svg = (event.currentTarget as HTMLElement).closest('svg');
    if (!svg) return;
    
    const rect = svg.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const index = this.getDataPointAtX(metricKey, x);
    
    if (index >= 0) {
      const syntheticEvent = {
        ...event,
        currentTarget: svg
      } as MouseEvent;
      this.showTooltip(syntheticEvent, metricKey, index);
    } else {
      this.hideTooltip();
    }
  }

  getHealthWarning(metricKey: string): string | null {
    const data = this.getChartData(metricKey);
    if (data.length === 0) return null;

    switch (metricKey) {
      case 'calories':
        return this.getCaloriesWarning(data);
      case 'water':
        return this.getWaterWarning(data);
      case 'protein':
        return this.getProteinWarning(data);
      case 'fats':
        return this.getFatWarning(data);
      case 'carbs':
        return this.getCarbsWarning(data);
      case 'sugar':
        return this.getSugarWarning(data);
      case 'fiber':
        return this.getFiberWarning(data);
      default:
        return null;
    }
  }

  private getCaloriesWarning(data: any[]): string | null {
    if (data.length === 0) return null;

    const daysAbove20 = data.filter(day => {
      if (day.normal === 0) return false;
      return day.consumed > day.normal * 1.2;
    }).length;

    const daysBelowNorm = data.filter(day => {
      if (day.normal === 0) return false;
      return day.consumed < day.normal;
    }).length;

    const totalDays = data.length;
    const percentAbove20 = (daysAbove20 / totalDays) * 100;
    const percentBelowNorm = (daysBelowNorm / totalDays) * 100;

    if (percentAbove20 > 50) {
      return 'Excessive consumption can lead to obesity.';
    } else if (percentBelowNorm > 50) {
      return 'Malnutrition can lead to poor health.';
    }

    return null;
  }

  private getWaterWarning(data: any[]): string | null {
    if (data.length === 0) return null;

    const avgConsumed = data.reduce((sum, day) => sum + day.consumed, 0) / data.length;
    const avgNorm = data.reduce((sum, day) => sum + day.normal, 0) / data.length;

    if (avgNorm === 0) return null;

    const daysAbove40 = data.filter(day => {
      if (day.normal === 0) return false;
      return day.consumed > day.normal * 1.4;
    }).length;

    const totalDays = data.length;
    const percentAbove40 = (daysAbove40 / totalDays) * 100;

    if (percentAbove40 >= 50) {
      return 'Excessive water consumption can lead to the leaching of electrolytes from the body.';
    }

    if (avgConsumed < avgNorm * 0.8) {
      return 'Can lead to dry skin and dehydration.';
    }

    return null;
  }

  private getProteinWarning(data: any[]): string | null {
    if (data.length === 0) return null;

    const avgConsumed = data.reduce((sum, day) => sum + day.consumed, 0) / data.length;
    const avgNorm = data.reduce((sum, day) => sum + day.normal, 0) / data.length;

    if (avgNorm === 0) return null;

    if (avgConsumed < avgNorm * 0.8) {
      return 'Low consumption can lead to muscle loss.';
    }

    return null;
  }

  private getFatWarning(data: any[]): string | null {
    if (data.length === 0) return null;

    const avgConsumed = data.reduce((sum, day) => sum + day.consumed, 0) / data.length;
    const avgNorm = data.reduce((sum, day) => sum + day.normal, 0) / data.length;

    if (avgNorm === 0) return null;

    if (avgConsumed > avgNorm * 1.15) {
      return 'Risk of obesity.';
    }

    if (avgConsumed < avgNorm * 0.7) {
      return 'Risk of deficiency in fat-soluble vitamins such as A, D, and K.';
    }

    return null;
  }

  private getCarbsWarning(data: any[]): string | null {
    if (data.length === 0) return null;

    const avgConsumed = data.reduce((sum, day) => sum + day.consumed, 0) / data.length;
    const avgNorm = data.reduce((sum, day) => sum + day.normal, 0) / data.length;

    if (avgNorm === 0) return null;

    if (avgConsumed < avgNorm * 0.8) {
      return 'May be a decline in energy and a vitamin deficiency.';
    }

    return null;
  }

  private getSugarWarning(data: any[]): string | null {
    if (data.length === 0) return null;

    const avgConsumed = data.reduce((sum, day) => sum + day.consumed, 0) / data.length;
    const avgNorm = data.reduce((sum, day) => sum + day.normal, 0) / data.length;

    if (avgNorm === 0) return null;

    if (avgConsumed > avgNorm * 1.05) {
      return 'Risk of obesity and diabetes.';
    }

    return null;
  }

  private getFiberWarning(data: any[]): string | null {
    if (data.length === 0) return null;

    const avgConsumed = data.reduce((sum, day) => sum + day.consumed, 0) / data.length;
    const avgNorm = data.reduce((sum, day) => sum + day.normal, 0) / data.length;

    if (avgNorm === 0) return null;

    if (avgConsumed < avgNorm * 0.85) {
      return 'Disruption of the cryptobiota and a risk of diabetes.';
    }

    return null;
  }
}


