import { Component } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { BookingComponent } from "./booking/booking.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [MatToolbarModule, BookingComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = '基隆仁愛會眾公眾見證';
}
