import { fireEvent, render, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import UCSBOrganizationsIndexPage from "main/pages/UCSBOrganizations/UCSBOrganizationsIndexPage";

import { apiCurrentUserFixtures }  from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import { organizationsFixtures } from "fixtures/organizationsFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";
import mockConsole from "jest-mock-console";

const mockToast = jest.fn();
jest.mock('react-toastify', () => {
    const originalModule = jest.requireActual('react-toastify');
    return {
        __esModule: true,
        ...originalModule,
        toast: (x) => mockToast(x)
    };
});

describe("UCSBOrganizationsIndexPage tests", () => {

    const axiosMock =new AxiosMockAdapter(axios);

    const testId = "UCSBOrganizationsTable";

    const setupUserOnly = () => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    };

    const setupAdminUser = () => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.adminUser);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    };

    test("renders without crashing for regular user", () => {
        setupUserOnly();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/ucsborganizations/all").reply(200, []);

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <UCSBOrganizationsIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );


    });

    test("renders without crashing for admin user", () => {
        setupAdminUser();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/ucsborganizations/all").reply(200, []);

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <UCSBOrganizationsIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );


    });

    test("renders three diningCommon without crashing for regular user", async () => {
        setupUserOnly();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/ucsborganizations/all").reply(200, organizationsFixtures.threeOrganizations);

        const { getByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <UCSBOrganizationsIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(  () => { expect(getByTestId(`${testId}-cell-row-0-col-orgCode`)).toHaveTextContent("AEP"); } );
        expect(getByTestId(`${testId}-cell-row-1-col-orgCode`)).toHaveTextContent("SC");
        expect(getByTestId(`${testId}-cell-row-2-col-orgCode`)).toHaveTextContent("Hillel");

    });

    test("renders three reviews without crashing for admin user", async () => {
        setupAdminUser();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/ucsborganizations/all").reply(200, organizationsFixtures.threeOrganizations);

        const { getByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <UCSBOrganizationsIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(  () => { expect(getByTestId(`${testId}-cell-row-0-col-orgCode`)).toHaveTextContent("AEP"); } );
        expect(getByTestId(`${testId}-cell-row-1-col-orgCode`)).toHaveTextContent("SC");
        expect(getByTestId(`${testId}-cell-row-2-col-orgCode`)).toHaveTextContent("Hillel");

    });

    test("renders empty table when backend unavailable, user only", async () => {
        setupUserOnly();

        const queryClient = new QueryClient();
        axiosMock.onGet("/api/ucsborganizations/all").timeout();

        const restoreConsole = mockConsole();

        const { queryByTestId, _getByText } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <UCSBOrganizationsIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(() => { expect(axiosMock.history.get.length).toBeGreaterThanOrEqual(1); });
        restoreConsole();

        expect(queryByTestId(`${testId}-cell-row-0-col-code`)).not.toBeInTheDocument();
    });

    test("test what happens when you click delete, admin", async () => {
        setupAdminUser();

        const queryClient = new QueryClient();
        axiosMock.onGet("/api/ucsborganizations/all").reply(200, organizationsFixtures.threeOrganizations);
        axiosMock.onDelete("/api/ucsborganizations", {params: {orgCode: "AEP"}}).reply(200, "Organization with orgCode AEP was deleted");


        const { getByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <UCSBOrganizationsIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(() => { expect(getByTestId(`${testId}-cell-row-0-col-orgCode`)).toBeInTheDocument(); });

        expect(getByTestId(`${testId}-cell-row-0-col-orgCode`)).toHaveTextContent("AEP"); 


        const deleteButton = getByTestId(`${testId}-cell-row-0-col-Delete-button`);
        expect(deleteButton).toBeInTheDocument();
       
        fireEvent.click(deleteButton);

        await waitFor(() => { expect(mockToast).toBeCalledWith("Organization with orgCode AEP was deleted") });

    });

});
