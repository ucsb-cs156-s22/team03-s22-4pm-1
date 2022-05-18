import React from 'react';

import ReviewsTable from "main/components/Reviews/ReviewsTable";
import { reviewsFixtures } from 'fixtures/reviewsFixtures';

export default {
    title: 'components/Reviews/ReviewsTable',
    component: ReviewsTable
};

const Template = (args) => {
    return (
        <ReviewsTable {...args} />
    )
};

export const Empty = Template.bind({});

Empty.args = {
    dates: []
};

export const ThreeReviews= Template.bind({});

ThreeReviews.args = {
    dates: reviewsFixtures.threeReviews
};
