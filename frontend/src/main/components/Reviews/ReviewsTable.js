import OurTable, { _ButtonColumn } from "main/components/OurTable";
//import { useBackendMutation } from "main/utils/useBackend";
//import { cellToAxiosParamsDelete, onDeleteSuccess } from "main/utils/UCSBDateUtils"
//import { useNavigate } from "react-router-dom";
//import { hasRole } from "main/utils/currentUser";

export default function ReviewsTable({ dates, _currentUser }) {

    /*const navigate = useNavigate();

    const editCallback = (cell) => {
        navigate(`/reviews/edit/${cell.row.values.id}`)
    }

    // Stryker disable all : hard to test for query caching
    const deleteMutation = useBackendMutation(
        cellToAxiosParamsDelete,
        { onSuccess: onDeleteSuccess },
        ["/api/reviews/all"]
    );
    // Stryker enable all 

    // Stryker disable next-line all : TODO try to make a good test for this
    const deleteCallback = async (cell) => { deleteMutation.mutate(cell); }
     */

    const columns = [
        {
            Header: 'id',
            accessor: 'id', // accessor is the "key" in the data
        },
        {
            Header: 'Reviewer Email',
            accessor: 'reviewerEmail',
        },
        {
            Header: 'Stars',
            accessor: 'stars',
        },
        {
            Header: 'Date Reviewed',
            accessor: 'dateReviewed',
        },
        {
            Header: 'Comments',
            accessor: 'comments',
        }
];

    /*
    const columnsIfAdmin = [
        ...columns,
        ButtonColumn("Edit", "primary", editCallback, "ReviewsTable"),
        ButtonColumn("Delete", "danger", deleteCallback, "ReviewsTable")
    ];
    */

    //const columnsToDisplay = hasRole(currentUser, "ROLE_ADMIN") ? columnsIfAdmin : columns;
    const columnsToDisplay = columns;

    return <OurTable
        data={dates}
        columns={columnsToDisplay}
        testid={"ReviewsTable"}
    />;
};
