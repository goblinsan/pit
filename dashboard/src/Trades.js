import React from "react";
import {Table} from "react-bootstrap";

class TradeRow extends React.Component {
    render() {
        const trade = this.props.trade;
        return (
            <tr>
                <td>{trade.requester}</td>
                <td>{trade.owner}</td>
                <td>{trade.amount}</td>
            </tr>
        );
    }
}

class TradeTable extends React.Component {
    render() {
        const rows = [];

        this.props.trades.forEach((trade) => {
            rows.push(
                <TradeRow
                    trade={trade}
                    key={trade.requester}/>
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

export default TradeTable;