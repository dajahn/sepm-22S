import {Address} from './address';
import {UserStatus} from '../enums/user-status';

export interface CreateUpdateUser {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  address: Address;
  role: 'CUSTOMER'|'ADMIN';
  status: UserStatus;
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  address: Address;
  role: 'CUSTOMER'|'ADMIN';
  status: UserStatus;
  lastNewsRead: Date;
}

