/*import BasicLayout from "main/layouts/BasicLayout/BasicLayout";

export default function ReviewsIndexPage() {
  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Reviews</h1>
        <p>
          This is where the index page will go
        </p>
      </div>
    </BasicLayout>
  )
}*/

import React from 'react'
import { useBackend } from 'main/utils/useBackend'; // use prefix indicates a React Hook

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import ReviewsTable from 'main/components/Reviews/ReviewsTable';
import { useCurrentUser } from 'main/utils/currentUser' // use prefix indicates a React Hook

export default function ReviewsIndexPage() {

  const currentUser = useCurrentUser();

  const { data: dates, error: _error, status: _status } =
    useBackend(
      // Stryker disable next-line all : don't test internal caching of React Query
      ["/api/MenuItemReview/all"], { method: "GET", url: "/api/MenuItemReview/all" }, []
    );

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Reviews</h1>
        <ReviewsTable dates={dates} currentUser={currentUser} />
      </div>
    </BasicLayout>
  )
}
