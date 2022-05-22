import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'capitalizeFirst'
})
export class CapitalizeFirstPipe implements PipeTransform {

  transform(value: string): string {
    return value?.split(' ').map(part => part.length === 0 ? part : part[0].toUpperCase() + part.slice(1).toLowerCase()).join(' ');
  }

}
