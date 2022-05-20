import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'duration'
})
export class DurationPipe implements PipeTransform {

  transform(value: string): string {
    const hours = value.split('H')[0] ? value.split('H')[0].split('PT')[1] : 0;
    let min = value.split('H')[1] ? value.split('H')[1].split('M')[0] : '0';
    console.log(value.split('M')[1]);
    if (min.length === 1) {
      min = '0' + min;
    }
    return `${hours}h:${min}m`;
}
}
