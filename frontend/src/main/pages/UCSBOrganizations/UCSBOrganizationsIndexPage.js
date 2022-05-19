import React from 'react'
import { useBackend } from 'main/utils/useBackend'; // use prefix indicates a React Hook

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import UCSBOrganizationsTable from 'main/components/UCSBOrganizations/UCSBOrganizationsTable';
import { useCurrentUser } from 'main/utils/currentUser';

export default function UCSBOrganizationsIndexPage() {
  
  const currentUser = useCurrentUser();

  const {data: orgs, error: _error, status: _status} = 
    useBackend(
      // Stryker disable next-line all : don't test internal caching of React Query
      ["/api/ucsborganizations/all"], { method: "GET", url: "/api/ucsborganizations/all" },[]
    );
  
  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>UCSB Organizations</h1>
        <p>
          <UCSBOrganizationsTable orgs = {orgs} currentUser={currentUser} />
        </p>
      </div>
    </BasicLayout>
  )
}