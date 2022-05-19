import { UserStatus } from './../enums/user-status';
import { Address } from './address';
import {BackendUserRoles} from '../enums/backend-user-roles';

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
  role: BackendUserRoles;
  status: UserStatus;
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  address: Address;
  role: BackendUserRoles;
  status: UserStatus;
  lastNewsRead: Date;
}

