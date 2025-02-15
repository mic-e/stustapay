from dataclasses import dataclass
from typing import Optional

from stustapay.core.util import _to_string_nullable


@dataclass
class NewTerminal:
    name: str
    description: Optional[str]
    tseid: Optional[str]
    active_shift: Optional[str]
    active_profile: Optional[int]
    active_cashier: Optional[int]


@dataclass
class Terminal(NewTerminal):
    id: int
    is_logged_in: bool
    registration_uuid: Optional[str]

    @classmethod
    def from_db(cls, row) -> "Terminal":
        return cls(
            id=row["id"],
            name=row["name"],
            description=row["description"],
            is_logged_in=row["session_uuid"] is not None,
            registration_uuid=_to_string_nullable(row["registration_uuid"]),
            tseid=row["tseid"],
            active_shift=row["active_shift"],
            active_profile=row["active_profile"],
            active_cashier=row["active_cashier"],
        )
