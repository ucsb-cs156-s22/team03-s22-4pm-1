import React from 'react';

import UCSBOrganizationsTable from 'main/components/UCSBOrganizations/UCSBOrganizationsTable';
import { organizationsFixtures } from 'fixtures/organizationsFixtures';

export default {
    title: 'components/UCSBOrganizations/UCSBOrganizationsTable',
    component: UCSBOrganizationsTable
};

const Template = (args) => {
    return (
        <UCSBOrganizationsTable {...args} />
    )
};

export const Empty = Template.bind({});

Empty.args = {
    orgs: []
};

export const ThreeDates = Template.bind({});

ThreeDates.args = {
    orgs: organizationsFixtures.threeOrganizations
};


