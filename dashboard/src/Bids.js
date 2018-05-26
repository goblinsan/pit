import React from "react";
import {Table} from "react-bootstrap";

class BidRow extends React.Component {
    render() {
        const bid = this.props.bid;
        return (
            <tr>
                <td>{bid.requester.name}</td>
                <td>{bid.owner.name}</td>
                <td>{bid.amount}</td>
            </tr>
        );
    }
}

class BidTable extends React.Component {
    render() {
        const rows = [];

        this.props.bids.forEach((bid) => {
            rows.push(
                <BidRow
                    bid={bid}
                    key={bid.requester}/>
            );
        });

        return (
            <Table>
                <thead>
                <tr>
                    <th>Requester</th>
                    <th>Owner</th>
                    <th>Amount</th>
                </tr>
                </thead>
                <tbody>{rows}</tbody>
            </Table>
        );
    }
}

export default BidTable;