import {Address} from './address';

export interface Location {
  id: number;
  name: string;
  address: Address;
  sectors: any; //TODO add correct sector class
  performances?: Performance[];
}
