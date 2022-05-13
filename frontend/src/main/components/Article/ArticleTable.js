import OurTable from "main/components/OurTable";
// import { useBackendMutation } from "main/utils/useBackend";
// import {  onDeleteSuccess } from "main/utils/UCSBDateUtils"
// import { useNavigate } from "react-router-dom";
// import { hasRole } from "main/utils/currentUser";


// export function cellToAxiosParamsDelete(cell) {
//     return {
//         url: "/api/ucsbdiningcommons",
//         method: "DELETE",
//         params: {
//             code: cell.row.values.code
//         }
//     }
// }

export default function ArticleTable({ article, _currentUser }) {

    // const navigate = useNavigate();

    // const editCallback = (cell) => {
    //     navigate(`/ucsbdates/edit/${cell.row.values.id}`)
    // }

    // Stryker disable all : hard to test for query caching
    // const deleteMutation = useBackendMutation(
    //     cellToAxiosParamsDelete,
    //     { onSuccess: onDeleteSuccess },
    //     ["/api/ucsbdiningcommons/all"]
    // );
    // Stryker enable all 

    // Stryker disable next-line all : TODO try to make a good test for this
    // const deleteCallback = async (cell) => { deleteMutation.mutate(cell); }

    const columns = [
        {
            Header: 'ID',
            accessor: 'id', 
        },
        {
            Header: 'Title',
            accessor: 'title',
        },
        {
            Header: 'URL',
            id: 'url', 
        },
        {
            Header: 'Explanation',
            id: 'explanation', // needed for tests
        },
        {
            Header: 'Email',
            id: 'email', // needed for tests
        },
        {
            Header: 'Date Added',
            accessor: 'dateAdded',
        }
    ];

    const testid = "ArticleTable";

    // const columnsIfAdmin = [
    //     ...columns,
    //     // ButtonColumn("Edit", "primary", editCallback, testid),
    //     ButtonColumn("Delete", "danger", deleteCallback, testid)
    // ];

    // const columnsToDisplay = hasRole(currentUser, "ROLE_ADMIN") ? columnsIfAdmin : columns;
    const columnsToDisplay = columns;


    return <OurTable
        data={article}
        columns={columnsToDisplay}
        testid={testid}
    />;
};