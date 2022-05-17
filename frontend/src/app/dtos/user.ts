import { Address } from './address';
import { UserStatus } from '../enums/user-status';

export interface CreateUser {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  address: Address;
  role: 'CUSTOMER' | 'ADMIN';
  status: UserStatus.OK;
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  address: Address;
  role: 'CUSTOMER' | 'ADMIN';
  status: UserStatus;
  lastNewsRead: Date;
}

