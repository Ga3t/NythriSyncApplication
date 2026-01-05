import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';

console.log('Starting Angular application...');

platformBrowserDynamic().bootstrapModule(AppModule)
  .then(() => {
    console.log('Angular application started successfully!');
  })
  .catch(err => {
    console.error('Error starting Angular application:', err);
  });


