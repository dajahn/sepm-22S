import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Stats} from '../../dtos/stats';
import {StatsService} from '../../services/stats.service';
import {ToastService} from '../../services/toast-service.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  stats: Stats;



  constructor(
    public authService: AuthService,
    private statsService: StatsService,
    private toastService: ToastService
  ) { }

  ngOnInit() {
    this.statsService.getStats().subscribe({
      next: (stats: Stats) => {
        console.log(stats);
        this.stats = stats;
      },
      error: err => {
        console.error('Error fetching stats', err);
        this.showDanger('Sorry, something went wrong. Could not load the stats 😔');
      }
    });
  }



  /**
   * Displays message on a failure.
   */
  showDanger(msg: string) {
    this.toastService.show(msg, {classname: 'bg-danger', delay: 5000});
  }
}
