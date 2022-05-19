import { UserStatus } from './../enums/user-status';
import { Address } from './address';

export interface UserSearchDto {
  status: UserStatus;
  role: 'CUSTOMER' | 'ADMIN';
  nameSearch: string;
}

export interface CreateUpdateUser {
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

