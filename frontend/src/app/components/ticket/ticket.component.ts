import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-ticket',
  templateUrl: './ticket.component.html',
  styleUrls: ['./ticket.component.scss']
})
export class TicketComponent implements OnInit {

  @Input() imageSrc: string;
  @Input() title: string;
  @Input() datetime: string;
  @Input() performance: string;
  @Input() rowTitle: string; // e.g. ROW or STANDING
  @Input() rowValue: string; // e.g. 1 or Sector 1
  @Input() columnTitle: string; // e.g. SEAT
  @Input() columnValue: string; // e.g. 1
  @Input() price: number;
  @Input() ticketClass: string;
  @Input() removeButton: boolean;
  @Input() showGreenBorder = false; // if true, a green border around the ticket is shown
  @Input() hoverTitle = 'Inspect'; // title for hovering a ticket

  @Output() remove = new EventEmitter(); // when remove button is clicked
  @Output() inspect = new EventEmitter(); // when the ticket is clicked



  constructor() { }

  ngOnInit(): void {
  }



  /**
   * Returns the corresponding design depending on the ticketClass variable.
   * If ticketClass equals 'VIP', the VIP design is returned.
   * If ticketClass equals 'Premium', the PR design is returned.
   * If ticketClass equals anything else, the default design is returned.
   *
   * @return json with the design elements
   */
  classStyle(): {acronym: string; emoji: string; background: string} {
    switch (this.ticketClass) {
      case 'VIP':
        return {
          acronym: 'VIP',
          emoji: '💎',
          background: 'var(--gradient-rainbow)'
        };
      case 'PREMIUM':
        return {
          acronym: 'PR',
          emoji: '🔥',
          background: 'var(--gradient-orange)'
        };
      default:
        return {
          acronym: '',
          emoji: '',
          background: 'var(--bg-tertiary)'
        };
    }
  }

}
